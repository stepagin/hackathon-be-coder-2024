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


    @JmsListener(destination = queueName+"Save")
    public void createPerson(Message message) {
        PersonEntity person = message.getPerson();
        personRepository.save(person);
    }

    @JmsListener(destination = queueName+"GetById")
    public void getPersonById(Message message) {
        PersonEntity person = message.getPerson();
        person = personRepository.findById(person.getId()).orElse(null);
        jmsTemplate.convertAndSend(queueName+"GetById", new Message(person));
    }

    @JmsListener(destination = queueName+"GetByLogin")
    public void getPersonByLogin(Message message) {
        PersonEntity person = message.getPerson();
        person = personRepository.findByLogin(person.getLogin());
        jmsTemplate.convertAndSend(queueName+"GetByLogin", new Message(person));
    }

}
