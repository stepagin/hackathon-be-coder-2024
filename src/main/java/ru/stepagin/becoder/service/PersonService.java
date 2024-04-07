package ru.stepagin.becoder.service;

import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

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
}
