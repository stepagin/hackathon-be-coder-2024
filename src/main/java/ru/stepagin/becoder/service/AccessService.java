package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public boolean checkHasAccess(Long personId, UUID accountId) {
        Message request = new Message(new AccessEntity(new PersonEntity(personId), new LegalAccountEntity(accountId)));
        jmsTemplate.convertAndSend(queueName + "CheckAccessRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "CheckAccessResponse");
        if (response != null) {
            return true;
        } else {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
    }



    public void save(AccessEntity access){
        Message request = new Message(access);
        jmsTemplate.convertAndSend(queueName + "SaveRequest", request);
//        accessRepository.save(access);
    }


    public List<AccessDTO> getAllByUserId(Long id){
        Message request = new Message(new PersonEntity(id));
        jmsTemplate.convertAndSend(queueName + "GetListByUserIdRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetListByUserIdResponse");

        return response.getList()
                .stream()
                .map(x -> (AccessEntity)x)
                .map(x -> new AccessDTO(x.getAccount().getId().toString()))
                .collect(Collectors.toList());
//        return accessRepository.findAll()
//                .stream()
//                .filter(x -> x.getPerson().getId().equals(id))
//                .map(x -> new AccessDTO(x.getId(), x.getAccount().getId().toString()))
//                .collect(Collectors.toList());
    }

}
