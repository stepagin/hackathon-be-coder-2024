package ru.stepagin.becoder.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

@Service
@AllArgsConstructor
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public PersonDTO getPersonById(Long id) {
        PersonEntity personEntity = null;
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
        PersonEntity personEntity = null;
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
