package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.UUID;

@Slf4j
@Service
@Component
@EnableJms
public class AccessService {

    private final JmsTemplate jmsTemplate;

    private final String queueName = "Access";

    public AccessService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = queueName+"CheckAccess")
    public boolean checkHasAccess(Long personId, UUID accountId) {
        Message message = new Message(new AccessEntity(personId, accountId));
        Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
//        access = accessRepository.findByAccount_IdAndPersonId(accountId, personId);

        if (response != null) {
            return true;
        } else {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
    }

}
