package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.time.LocalDateTime;

@Slf4j
@Service
public class HistoryService {
    private final JmsTemplate jmsTemplate;
    private final String queueName = "History";
    public HistoryService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    public void addRecord(@Nonnull Long amount, @Nonnull LegalAccountEntity legalAccountEntity, @Nonnull Boolean success) {
        Message message = new Message(new HistoryEntity(amount,legalAccountEntity,success));
        try {
            jmsTemplate.convertAndSend(queueName+"Save", message);
//            historyRepository.save(historyEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Произошла ошибка во время сохранения в историю запросов");
        }
    }
}
