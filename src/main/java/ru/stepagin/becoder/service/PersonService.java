package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.config.SecurityConfiguration;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;

@Slf4j
@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public PersonDTO getPersonById(Long id) {
        PersonEntity personEntity;
        try {
            personEntity = personRepository.findById(id).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }

        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с заданным id");

        return new PersonDTO(personEntity);
    }

    public PersonDTO getPersonByLogin(String login) {
        PersonEntity personEntity;
        try {
            personEntity = personRepository.findByLogin(login);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }

        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с указаным login");

        return new PersonDTO(personEntity);
    }

    public PersonDTO registerPerson(String login, String password) {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Login and password cannot be null");
        }
        if (login.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Login and password cannot be empty");
        }
        if (login.length() < 6 || password.length() < 6) {
            throw new IllegalArgumentException("Login and password must contain at least 6 characters");
        }
        if (personRepository.findByLogin(login) != null) {
            throw new IllegalArgumentException("Login already exists");
        }
        PersonEntity personEntity = new PersonEntity();
        personEntity.setLogin(login);
        personEntity.setPassword(SecurityConfiguration.passwordEncoder().encode(password));
        personRepository.save(personEntity);
        return new PersonDTO(personEntity);
    }

    public List<PersonDTO> getAllUsers() {
        return personRepository.findAll().stream().map(PersonDTO::new).toList();
    }
}
