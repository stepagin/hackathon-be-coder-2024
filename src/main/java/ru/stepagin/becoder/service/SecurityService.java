package ru.stepagin.becoder.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

@Service
public class SecurityService {
    private final JmsTemplate jmsTemplate;

    private final String queueName = "Person";

    public SecurityService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = queueName+"GetByLogin")
    public boolean isAuthorized(Long id, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Message message = new Message(new PersonEntity(user.getUsername()));
        Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
        PersonEntity person = response.getPerson();
//        PersonEntity person = personRepository.findByLogin(user.getUsername());
        if (person == null) return false;
        return person.getId() != null && person.getId().equals(id);
    }

}
