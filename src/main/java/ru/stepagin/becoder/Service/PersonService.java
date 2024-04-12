package ru.stepagin.becoder.Service;


import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;
import ru.stepagin.becoder.repository.LegalAccountRepository;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableJms
public class PersonService {

    private final PersonRepository personRepository;

    private final JmsTemplate jmsTemplate;


    private final String queueName = "Person";

    public PersonService(JmsTemplate jmsTemplate, PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.jmsTemplate = jmsTemplate;
    }


    @JmsListener(destination = queueName + "SaveRequest")
    public void createPerson(Message message) {
        PersonEntity person = message.getPerson();
        personRepository.save(person);
    }

    @JmsListener(destination = queueName + "GetByIdRequest")
    public void getPersonById(Message message) {
        PersonEntity person = message.getPerson();
        person = personRepository.findById(person.getId()).orElse(null);
        jmsTemplate.convertAndSend(queueName + "GetByIdResponse", new Message(person));
    }

    @JmsListener(destination = queueName + "GetByLoginRequest")
    public void getPersonByLogin(Message message) {
        PersonEntity person = personRepository.findByLogin(message.getPerson().getLogin());
        jmsTemplate.convertAndSend(queueName + "GetByLoginResponse", new Message(person));
    }

    @JmsListener(destination = queueName + "GetByLoginRegisterRequest")
    public void getPersonByLoginRegister(Message message) {
        PersonEntity person = personRepository.findByLogin(message.getPerson().getLogin());
        jmsTemplate.convertAndSend(queueName + "GetByLoginRegisterResponse", new Message(person));
    }

    @JmsListener(destination = queueName + "GetListRequest")
    public void getAllUsers() {
        List<Object> to_ret = new ArrayList<>(personRepository.findAll());
        Message response = new Message(to_ret);
        jmsTemplate.convertAndSend(queueName + "GetListResponse", response);
    }
}
