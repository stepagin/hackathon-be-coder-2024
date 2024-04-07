package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.LegalAccountRepository;

@Service
public class LegalAccountService {
    private final LegalAccountRepository legalAccountRepository;
    private final HistoryService historyService;
    private final AccessService accessService;

    public LegalAccountService(LegalAccountRepository legalAccountRepository, HistoryService historyService, AccessService accessService) {
        this.legalAccountRepository = legalAccountRepository;
        this.historyService = historyService;
        this.accessService = accessService;
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check user has access
        if (!accessService.checkHasAccess(balanceChange.getAccount().getId(), balanceChange.getAccount().getId()))
            throw new IllegalArgumentException("У данного пользователя нет доступа к этому счёту");

        // check decreasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

        LegalAccountEntity account = this.getAccountEntityById(balanceChange.getAccount().getId());
        Long balance = account.getBalance();
        // check balance will not become negative
        if (balance - balanceChange.getAmount() < 0) {
            historyService.addRecord(balanceChange.getAmount(), account, false);
            // at other way save in history with success=false
            throw new IllegalArgumentException("На счету недостаточно средств");
        }
        // update balance
        legalAccountRepository.updateBalance(
                balanceChange.getAccount().getId(),
                balance - balanceChange.getAmount() // decreasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void increaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check decreasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

        LegalAccountEntity account = this.getAccountEntityById(balanceChange.getAccount().getId());

        Long balance = this.getBalanceById(balanceChange.getAccount().getId());
        // update balance
        legalAccountRepository.updateBalance(
                balanceChange.getAccount().getId(),
                balance + balanceChange.getAmount() // increasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    public Long getBalanceById(Long id) {
        LegalAccountEntity account;
        account = this.getAccountEntityById(id);
        if (account == null)
            throw new IllegalArgumentException("Не существует аккаунта с заданным id");
        return account.getBalance();
    }

    public LegalAccountEntity getAccountEntityById(@Nonnull Long accountId) {
        LegalAccountEntity legalAccountEntity;
        try {
            legalAccountEntity = legalAccountRepository.findById(accountId).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }
        if (legalAccountEntity == null) {
            throw new IllegalArgumentException("Не найден счёт с данным id");
        }
        return legalAccountEntity;
    }
}
