package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LegalAccountService {
    private final LegalAccountRepository legalAccountRepository;
    private final HistoryService historyService;

    public LegalAccountService(LegalAccountRepository legalAccountRepository, HistoryService historyService) {
        this.legalAccountRepository = legalAccountRepository;
        this.historyService = historyService;
    }

    @Transactional
    public LegalAccountDTO createAccount() {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity();
        legalAccountEntity.setBalance(0L);
        legalAccountEntity = legalAccountRepository.save(legalAccountEntity);
        return new LegalAccountDTO(legalAccountEntity);
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // getting account from DB and its balance
        LegalAccountEntity account = this.getAccountEntityById((balanceChange.getAccount().getId()));
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
        LegalAccountEntity account = this.getAccountEntityById((balanceChange.getAccount().getId()));

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() + balanceChange.getAmount() // increasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void updateBalance(UUID accountId, Long balance) {
        legalAccountRepository.updateBalanceByAccountId(
                accountId,
                balance
        );

    }

    public LegalAccountEntity getAccountEntityById(@Nonnull String id) {
        LegalAccountEntity legalAccountEntity;
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось распарсить uuid");
        }

        try {
            legalAccountEntity = legalAccountRepository.findById(uuid).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }
        if (legalAccountEntity == null) {
            throw new IllegalArgumentException("Не найден счёт с данным id");
        }
        return legalAccountEntity;
    }

    public LegalAccountDTO getAccountById(@Nonnull String id) {
        return new LegalAccountDTO(this.getAccountEntityById(id));
    }

    public List<LegalAccountDTO> getAllAccounts() {
        return legalAccountRepository.findAll().stream().map(LegalAccountDTO::new).toList();
    }
}
