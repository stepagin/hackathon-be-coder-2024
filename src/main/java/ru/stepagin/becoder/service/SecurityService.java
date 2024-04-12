package ru.stepagin.becoder.service;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

@Service
@Component
@EnableJms
public class SecurityService {
    private final JmsTemplate jmsTemplate;

    private final String queueName = "Person";

    public SecurityService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public boolean isAuthorized(Long id, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Message request = new Message(new PersonEntity(user.getUsername()));
        jmsTemplate.convertAndSend(queueName + "GetByLoginRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByLoginResponse");
        PersonEntity person = response.getPerson();
        if (person == null) return false;
        return person.getId() != null && person.getId().equals(id);
    }

}
