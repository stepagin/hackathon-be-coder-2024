package ru.stepagin.becoder.Service;


import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.microservicesConnection.Message;
import ru.stepagin.becoder.repository.AccessRepository;

@Component
@EnableJms
public class AccessService {

    private final JmsTemplate jmsTemplate;
    private final AccessRepository accessRepository;

    private final String queueName = "Access";

    public AccessService(JmsTemplate jmsTemplate, AccessRepository accessRepository) {
        this.jmsTemplate = jmsTemplate;
        this.accessRepository = accessRepository;
    }


    @JmsListener(destination = queueName + "CheckAccessRequest")
    public void checkHasAccess(Message message) {
        AccessEntity access = accessRepository.findByAccount_IdAndPersonId(message.getAccess().getAccount().getId(), message.getAccess().getPerson().getId());
        Message to_ret = new Message(access);
        jmsTemplate.convertAndSend(queueName + "CheckAccessResponse", to_ret);
    }


}
