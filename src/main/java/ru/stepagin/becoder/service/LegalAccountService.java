package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.LegalAccountRepository;

@Slf4j
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
    public LegalAccountDTO createAccount(@Nonnull Long id) {
        LegalAccountEntity legalAccountEntity;
        legalAccountEntity = legalAccountRepository.findById(id).orElse(null);
        if (legalAccountEntity != null) {
            throw new IllegalArgumentException("Legal account already exists");
        }
        legalAccountEntity = new LegalAccountEntity();
        legalAccountEntity.setId(id);
        legalAccountEntity.setBalance(0L);
        legalAccountRepository.save(legalAccountEntity);
        return new LegalAccountDTO(legalAccountEntity);
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check user has access
        if (!accessService.checkHasAccess(balanceChange.getAccount().getId(), balanceChange.getAccount().getId()))
            throw new IllegalArgumentException("У данного пользователя нет доступа к этому счёту");

        // getting account from DB and its balance
        LegalAccountEntity account = this.getAccountEntityById(balanceChange.getAccount().getId());
        Long balance = account.getBalance();

        // check balance will not become negative
        if (balance - balanceChange.getAmount() < 0) {
            historyService.addRecord(balanceChange.getAmount(), account, false);
            // save in history with success=false
            throw new IllegalArgumentException("На счету недостаточно средств");
        }

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() - balanceChange.getAmount() // decreasing
        );

        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void increaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        LegalAccountEntity account = this.getAccountEntityById(balanceChange.getAccount().getId());

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() + balanceChange.getAmount() // increasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void updateBalance(Long accountId, Long balance) {
        legalAccountRepository.updateBalanceByAccountId(
                accountId,
                balance
        );

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

    public LegalAccountDTO getAccountById(@Nonnull Long accountId) {
        LegalAccountEntity legalAccountEntity;
        try {
            legalAccountEntity = legalAccountRepository.findById(accountId).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }
        if (legalAccountEntity == null) {
            throw new IllegalArgumentException("Не найден счёт с данным id");
        }
        return new LegalAccountDTO(legalAccountEntity);
    }
}
