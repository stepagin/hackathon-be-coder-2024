package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

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


    public PersonDTO getPersonByLogin(String login) {
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

        return new PersonDTO(personEntity);
    }
}
