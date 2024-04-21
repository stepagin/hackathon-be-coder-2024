package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.Objects;
import java.util.UUID;

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
    public boolean isActiveOwner(PersonEntity person, UUID accountId) {
        LegalAccountEntity account = getAccountEntityById(accountId.toString());
        return accessService.checkHasAccess(person.getId(), accountId) && (Objects.equals(account.getCreator(), person));
    }

    @Transactional
    public LegalAccountDTO createAccount(PersonEntity person) {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(person);
        legalAccountEntity = legalAccountRepository.save(legalAccountEntity);
        accessService.save(new AccessEntity(person, legalAccountEntity));
        return new LegalAccountDTO(legalAccountEntity);
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check decreasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

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
        legalAccountRepository.updateBalanceByAccountId(accountId, balance);
    }

    @Transactional
    public LegalAccountEntity getAccountEntityById(@Nonnull String id) {
        LegalAccountEntity legalAccountEntity = legalAccountRepository.findById(UUID.fromString(id)).orElse(null);
        if (legalAccountEntity == null) {
            throw new InvalidIdSuppliedException("Не найден счёт с данным id");
        }
        return legalAccountEntity;
    }

    @Transactional
    public LegalAccountDTO getAccountById(@Nonnull String id) {
        return new LegalAccountDTO(this.getAccountEntityById(id));
    }
}
