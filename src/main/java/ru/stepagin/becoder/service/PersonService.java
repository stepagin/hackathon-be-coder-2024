package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
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

    public PersonDTO registerPerson(String login, String password) {
        if (personRepository.existsByLoginAllIgnoreCase(login)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже зарегистрирован");
        }
        PersonEntity personEntity = new PersonEntity(login, password);
        personRepository.save(personEntity);
        return new PersonDTO(personEntity);
    }

    public List<PersonDTO> getAllUsers() {
        return personRepository.findAll().stream().map(PersonDTO::new).toList();
    }
}
