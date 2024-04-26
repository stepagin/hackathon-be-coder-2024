package ru.stepagin.becoder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.mappers.PersonMapper;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public PersonDto registerPerson(String login, String password) {
        if (personRepository.existsByLoginAllIgnoreCase(login)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже зарегистрирован");
        }
        PersonEntity personEntity = new PersonEntity(login, password);
        personRepository.save(personEntity);
        return PersonMapper.toDto(personEntity);
    }

    public List<PersonDto> getAllUsers() {
        return PersonMapper.toDtos(personRepository.findAll());
    }

    public PersonDto getUser(String login) {
        return PersonMapper.toDto(personRepository.findByLogin(login));
    }

    public PersonEntity getPersonEntity(String login) {
        return personRepository.findByLogin(login);
    }
}
