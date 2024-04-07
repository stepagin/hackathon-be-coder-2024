package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.LegalAccountRepository;

@Service
@AllArgsConstructor
public class LegalAccountService {
    @Autowired
    private LegalAccountRepository legalAccountRepository;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private AccessService accessService;


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check user has access
        if (!accessService.checkHasAccess(balanceChange.getAccount().getId(), balanceChange.getAccount().getId()))
            throw new IllegalArgumentException("У данного пользователя нет доступа к этому счёту");

        // check decreasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

        Long balance = this.getBalanceById(balanceChange.getAccount().getId());
        // check balance will not become negative
        if (balance - balanceChange.getAmount() < 0) {
            historyService.addRecord(balanceChange.getAmount(), balanceChange.getAccount().getId(), false);
            // at other way save in history with success=false
            throw new IllegalArgumentException("На счету недостаточно средств");
        }
        // update balance
        legalAccountRepository.updateBalance(
                balanceChange.getAccount().getId(),
                balance - balanceChange.getAmount() // decreasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), balanceChange.getAccount().getId(), true);
    }

    @Transactional
    public void increaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check decreasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

        Long balance = this.getBalanceById(balanceChange.getAccount().getId());
        // update balance
        legalAccountRepository.updateBalance(
                balanceChange.getAccount().getId(),
                balance + balanceChange.getAmount() // increasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), balanceChange.getAccount().getId(), true);
    }

    public Long getBalanceById(Long id) {
        LegalAccountEntity account = null;
        try {
            account = legalAccountRepository.findById(id).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить данные счёта");
        }

        if (account == null)
            throw new IllegalArgumentException("Не существует аккаунта с заданным id");

        return account.getBalance();
    }

    @Transactional
    public HistoryEntity setLegalAccountById(@Nonnull HistoryEntity historyEntity, @Nonnull Long accountId) {
        LegalAccountEntity legalAccountEntity = null;
        try {
            legalAccountEntity = legalAccountRepository.findById(accountId).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }
        if (legalAccountEntity == null) {
            throw new IllegalArgumentException("Не найден счёт с данным id");
        }
        historyEntity.setAccount(legalAccountEntity);
        return historyEntity;
    }
}
