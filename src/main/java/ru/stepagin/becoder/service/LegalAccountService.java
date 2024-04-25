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
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalAccountService {
    private final LegalAccountRepository legalAccountRepository;
    private final HistoryService historyService;
    private final AccessRepository accessRepository;

    @Transactional
    public boolean isActiveOwner(PersonEntity person, UUID accountId) {
        LegalAccountEntity account = getAccountEntityById(accountId.toString());
        if (account == null)
            throw new InvalidIdSuppliedException("Указан неверный id счёта");
        return (accessRepository.findByAccount_IdAndPersonId(accountId, person.getId()) != null)
                && (Objects.equals(account.getCreator(), person));
    }

    @Transactional
    public LegalAccountDTO createAccount(PersonEntity person) {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(person);
        legalAccountEntity = legalAccountRepository.save(legalAccountEntity);
        accessRepository.save(new AccessEntity(person, legalAccountEntity));
        return new LegalAccountDTO(legalAccountEntity);
    }

    @Transactional
    public void decrease(String accountId, long amount){
        legalAccountRepository.decreaseBalanceById(UUID.fromString(accountId), amount);
    }
    @Transactional
    public LegalAccountDTO decreaseBalance(String accountId, long amount) {
        // getting account from DB and its balance
        this.decrease(accountId, amount);
        LegalAccountEntity account = this.getAccountEntityById(accountId);
//        // save in history with success=true
        historyService.addRecord(amount, account, true);
        return new LegalAccountDTO();
    }



    @Transactional
    public void increase(String accountId, long amount){
        legalAccountRepository.increaseBalanceById(UUID.fromString(accountId), amount);
    }

    @Transactional
    public LegalAccountDTO increaseBalance(String accountId, long amount) {
        // getting account from DB and its balance
        this.increase(accountId, amount);
        LegalAccountEntity account = this.getAccountEntityById(accountId);
//        // save in history with success=true
        historyService.addRecord(amount, account, true);
        return new LegalAccountDTO();
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
