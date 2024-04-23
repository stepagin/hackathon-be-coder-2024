package ru.stepagin.becoder.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.stepagin.becoder.entity.PersonEntity;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void findByLogin() {
        PersonEntity person = new PersonEntity("user", "user");
        personRepository.save(person);

        PersonEntity result = personRepository.findByLogin(person.getLogin());

        assertEquals(person, result);
    }
}