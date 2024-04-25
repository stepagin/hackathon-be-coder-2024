package ru.stepagin.becoder.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@RequiredArgsConstructor
public class LegalAccountService {
    private final LegalAccountRepository legalAccountRepository;
    private final HistoryService historyService;
    private final AccessService accessService;

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
    public LegalAccountDTO decreaseBalance(String accountId, long amount) {
        // getting account from DB and its balance
        LegalAccountEntity account = this.getAccountEntityById(accountId);
        Long balance = account.getBalance();

        // check balance will not become negative
        if (balance - amount < 0) {
            historyService.addRecord(amount, account, false);
            // save in history with success=false
            throw new IllegalArgumentException("На счету недостаточно средств");
        }

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() - amount // decreasing
        );

        // save in history with success=true
        historyService.addRecord(amount, account, true);
        account.setBalance(account.getBalance() - amount);
        return new LegalAccountDTO(account);
    }

    @Transactional
    public LegalAccountDTO increaseBalance(String accountId, long amount) {
        // getting account from DB and its balance
        LegalAccountEntity account = this.getAccountEntityById(accountId);

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() + amount // increasing
        );
        // save in history with success=true
        historyService.addRecord(amount, account, true);
        account.setBalance(account.getBalance() + amount);
        return new LegalAccountDTO(account);
    }

    @Transactional
    public void updateBalance(UUID accountId, Long balance) {
        legalAccountRepository.updateBalanceByAccountId(accountId, balance);
    }

    @Transactional
    public LegalAccountEntity getAccountEntityById(String id) {
        return legalAccountRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new InvalidIdSuppliedException("Не найден счёт с данным id"));
    }

    @Transactional
    public LegalAccountDTO getAccountById(String id) {
        return new LegalAccountDTO(this.getAccountEntityById(id));
    }
}
