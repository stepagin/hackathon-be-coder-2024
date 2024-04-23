package ru.stepagin.becoder.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class LegalAccountRepositoryTest {

    @Autowired
    private LegalAccountRepository legalAccountRepository;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void updateBalanceByAccountId() {
        PersonEntity person = new PersonEntity("user", "user");
        person = personRepository.save(person);

        LegalAccountEntity legalAccount = new LegalAccountEntity(person);
        legalAccount = legalAccountRepository.save(legalAccount);

        legalAccount.setBalance(100L);

        UUID uuid = legalAccount.getId();
        legalAccountRepository.updateBalanceByAccountId(legalAccount.getId(), 100L);

        LegalAccountEntity result = legalAccountRepository.findAll().stream().filter(t -> t.getId().equals(uuid)).findFirst().get();

        assertEquals(legalAccount, result);
    }
}