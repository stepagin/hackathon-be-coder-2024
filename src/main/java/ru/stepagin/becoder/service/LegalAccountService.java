package ru.stepagin.becoder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.Objects;
import java.util.Optional;
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
        return (accessRepository.findByAccountIdAndPersonId(accountId, person.getId()) != null)
                && (Objects.equals(account.getCreator(), person));
    }

    @Transactional
    public LegalAccountDTO createAccount(PersonEntity person) {
        if (person == null) {
            throw new IllegalArgumentException("Не найден пользователь, создающий счёт");
        }
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(person);
        legalAccountEntity = legalAccountRepository.save(legalAccountEntity);
        accessRepository.save(new AccessEntity(person, legalAccountEntity));
        return new LegalAccountDTO(legalAccountEntity);
    }


    @Transactional
    public void decreaseBalance(String accountId, long amount) {
        legalAccountRepository.decreaseBalanceById(UUID.fromString(accountId), amount);
    }

    @Transactional
    public LegalAccountEntity isEnough(String accountId, long amount) {
        LegalAccountEntity account = legalAccountRepository.findById(UUID.fromString(accountId)).orElseThrow(() -> new InvalidIdSuppliedException("Не найден счёт с данным id"));
        if (account.getBalance() - amount < 0)
            throw new IllegalArgumentException();
        return account;
    }

    @Transactional
    public void increaseBalance(String accountId, long amount) {
        legalAccountRepository.increaseBalanceById(UUID.fromString(accountId), amount);
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
