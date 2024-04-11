package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

@Slf4j
@Service
public class PersonService {
    private final JmsTemplate jmsTemplate;
    private final String queueName = "Person";
    public PersonService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = queueName+"GetById")
    public PersonDTO getPersonById(Long id) {
        PersonEntity personEntity;
        try {
//            personEntity = personRepository.findById(id).orElse(null);
            Message message = new Message(new PersonEntity(id));
            Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
            assert response != null;
            personEntity = response.getPerson();
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }

        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с заданным id");

        return new PersonDTO(personEntity);
    }


    @JmsListener(destination = queueName+"GetByLogin")
    public PersonDTO getPersonByLogin(String login) {
        PersonEntity personEntity;
        try {
//            personEntity = personRepository.findByLogin(login);
            Message message = new Message(new PersonEntity(login));
            Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
            assert response != null;
            personEntity = response.getPerson();
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось запросить пользователя");
        }

        if (personEntity == null)
            throw new IllegalArgumentException("Не существует пользователя с указаным login");

        return new PersonDTO(personEntity);
    }
}
