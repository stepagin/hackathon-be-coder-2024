package ru.stepagin.becoder.Service;


import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.microservicesConnection.Message;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.HistoryRepository;

@Component
@EnableJms
public class HistoryService {

    private final HistoryRepository historyRepository;

    private final String queueName = "History";

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }


    @JmsListener(destination = queueName + "SaveRequest")
    public void addRecord(Message message) {
        HistoryEntity history = message.getHistory();
        historyRepository.save(history);
    }


}
