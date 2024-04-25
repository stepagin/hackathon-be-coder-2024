package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;

@Slf4j
@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
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
        PersonEntity personEntity = new PersonEntity(login, password);
        personRepository.save(personEntity);
        return new PersonDTO(personEntity);
    }

    public PersonDTO findById(Long id){
        PersonEntity person = personRepository.findById(id).orElseThrow(() -> new InvalidIdSuppliedException("Нет такого пользователя"));
        return new PersonDTO(person);
    }

    public List<PersonDTO> getAllUsers() {
        return personRepository.findAll().stream().map(PersonDTO::new).toList();
    }
}
