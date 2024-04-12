package ru.stepagin.becoder.Service;


import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;
import ru.stepagin.becoder.repository.AccessRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    @JmsListener(destination = queueName + "SaveRequest")
    public void save(Message message){
        AccessEntity access = message.getAccess();
        accessRepository.save(access);
    }


    @JmsListener(destination = queueName + "GetListByUserIdRequest")
    public void getAllByUserId(Message message){
        Long id = message.getPerson().getId();

        List<Object> to_ret = accessRepository
                .findAll()
                .stream()
                .filter(x -> x.getPerson().getId().equals(id))
                .collect(Collectors.toList());

        Message request = new Message(to_ret);
        jmsTemplate.convertAndSend(queueName + "GetListByUserIdResponse", request);
    }

}
