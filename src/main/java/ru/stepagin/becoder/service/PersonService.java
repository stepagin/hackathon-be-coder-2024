package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.config.SecurityConfiguration;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Component
@EnableJms
public class PersonService {
    private final JmsTemplate jmsTemplate;
    private final String queueName = "Person";

    public PersonService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public PersonDTO getPersonById(Long id) {
        PersonEntity personEntity;
        try {
            Message request = new Message(new PersonEntity(id));
            jmsTemplate.convertAndSend(queueName + "GetByIdRequest", request);
            Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByIdResponse");
            personEntity = response.getPerson();
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }
        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с заданным id");

        return new PersonDTO(personEntity);
    }


    public PersonEntity getPersonByLogin(String login) {
        PersonEntity personEntity;
        try {
            Message request = new Message(new PersonEntity(login));
            jmsTemplate.convertAndSend(queueName + "GetByLoginRequest", request);
            Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByLoginResponse");
            personEntity = response.getPerson();
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }
        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с указаным login");

        return personEntity;
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
        System.out.println(login);
        Message request = new Message(new PersonEntity(login));
        jmsTemplate.convertAndSend(queueName + "GetByLoginRegisterRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByLoginRegisterResponse");
        if (response.getPerson() != null) {
            System.out.println(response.getPerson().getLogin());
            throw new IllegalArgumentException("Login already exists");
        }
        PersonEntity personEntity = new PersonEntity(login, SecurityConfiguration.passwordEncoder().encode(password));

//        personRepository.save(personEntity);
        request = new Message(personEntity);
        jmsTemplate.convertAndSend(queueName + "SaveRequest", request);
        return new PersonDTO(personEntity);
    }


    public List<PersonDTO> getAllUsers() {
        Message request = new Message();
        jmsTemplate.convertAndSend(queueName + "GetListRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetListResponse");

        return response.getList()
                .stream()
                .map(x -> (PersonEntity)x)
                .map(PersonDTO::new)
                .toList();

//        return personRepository.findAll().stream().map(PersonDTO::new).toList();
    }

}
