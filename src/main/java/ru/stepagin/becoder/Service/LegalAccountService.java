package ru.stepagin.becoder.Service;


import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.microservicesConnection.Message;
import ru.stepagin.becoder.repository.HistoryRepository;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.UUID;

@Component
@EnableJms
public class LegalAccountService {

    private final LegalAccountRepository legalAccountRepository;

    private final JmsTemplate jmsTemplate;


    private final String queueName = "LegalAccount";

    public LegalAccountService(JmsTemplate jmsTemplate, LegalAccountRepository legalAccountRepository) {
        this.legalAccountRepository = legalAccountRepository;
        this.jmsTemplate = jmsTemplate;
    }


    @JmsListener(destination = queueName + "Save")
    public void createAccount(Message message) {
        LegalAccountEntity legalAccount = message.getLegalAccount();
        legalAccount = legalAccountRepository.save(legalAccount);
        jmsTemplate.convertAndSend(queueName + "Save", new Message(legalAccount));
    }

    @JmsListener(destination = queueName + "Update")
    public void updateBalance(Message message) {
        LegalAccountEntity legalAccount = message.getLegalAccount();
        legalAccountRepository.updateBalanceByAccountId(
                legalAccount.getId(),
                legalAccount.getBalance()
        );

    }

    @JmsListener(destination = queueName + "GetById")
    public void getAccountEntityById(Message message) {
        LegalAccountEntity legalAccount = message.getLegalAccount();
        legalAccount = legalAccountRepository.findById(legalAccount.getId()).orElse(null);
        jmsTemplate.convertAndSend(queueName + "GetByIdGetter", new Message(legalAccount));
    }

}
