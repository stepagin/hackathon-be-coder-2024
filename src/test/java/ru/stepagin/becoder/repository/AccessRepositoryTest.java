package ru.stepagin.becoder.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AccessRepositoryTest {

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LegalAccountRepository legalAccountRepository;

    @Test
    void findByAccountIdAndPersonId() {
        PersonEntity person = new PersonEntity("user", "user");
        person = personRepository.save(person);

        LegalAccountEntity legalAccount = new LegalAccountEntity(person);
        legalAccount = legalAccountRepository.save(legalAccount);

        AccessEntity test = new AccessEntity(person, legalAccount);
        accessRepository.save(test);

        AccessEntity result = accessRepository.findByAccountIdAndPersonId(legalAccount.getId(), person.getId());

        assertEquals(test, result);
    }

    @Test
    void findByPerson() {
        PersonEntity person = new PersonEntity("user", "user");
        person = personRepository.save(person);

        LegalAccountEntity legalAccount1 = new LegalAccountEntity(person);
        legalAccount1 = legalAccountRepository.save(legalAccount1);

        LegalAccountEntity legalAccount2 = new LegalAccountEntity(person);
        legalAccount2 = legalAccountRepository.save(legalAccount2);

        AccessEntity access1 = new AccessEntity(person, legalAccount1);
        accessRepository.save(access1);
        AccessEntity access2  = new AccessEntity(person, legalAccount2);
        accessRepository.save(access2);

        List<AccessEntity> test = new ArrayList<>();
        test.add(access1);
        test.add(access2);

        List<AccessEntity> result = accessRepository.findByPerson(person);

        assertEquals(test, result);
    }
}