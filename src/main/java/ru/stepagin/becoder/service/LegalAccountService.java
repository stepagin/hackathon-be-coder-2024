package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.UUID;

@Slf4j
@Service
@Component
@EnableJms
public class LegalAccountService {
    private final JmsTemplate jmsTemplate;

    private final HistoryService historyService;
    private final AccessService accessService;
    private final String queueName = "LegalAccount";

    public LegalAccountService(JmsTemplate jmsTemplate, HistoryService historyService, AccessService accessService) {
        this.jmsTemplate = jmsTemplate;
        this.historyService = historyService;
        this.accessService = accessService;
    }

    @Transactional
    public LegalAccountDTO createAccount(PersonEntity person) {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(0L);
        Message request = new Message(legalAccountEntity);
        jmsTemplate.convertAndSend(queueName + "SaveRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "SaveResponse");
        accessService.save(new AccessEntity(person, legalAccountEntity));
        assert response != null;
        return new LegalAccountDTO(response.getLegalAccount());
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // getting account from DB and its balance
        LegalAccountEntity account = this.getAccountEntityById((balanceChange.getAccount().getId()));
        Long balance = account.getBalance();

        // check balance will not become negative
        if (balance - balanceChange.getAmount() < 0) {
            historyService.addRecord(balanceChange.getAmount(), account, false);
            // save in history with success=false
            throw new IllegalArgumentException("На счету недостаточно средств");
        }

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() - balanceChange.getAmount() // decreasing
        );

        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void increaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        LegalAccountEntity account = this.getAccountEntityById((balanceChange.getAccount().getId()));

        // update balance
        this.updateBalance(
                account.getId(),
                account.getBalance() + balanceChange.getAmount() // increasing
        );
        // save in history with success=true
        historyService.addRecord(balanceChange.getAmount(), account, true);
    }

    @Transactional
    public void updateBalance(UUID accountId, Long balance) {
        Message message = new Message(new LegalAccountEntity(accountId, balance));
        jmsTemplate.convertAndSend(queueName + "UpdateRequest", message);

    }


    public LegalAccountEntity getAccountEntityById(@Nonnull String id) {
        LegalAccountEntity legalAccountEntity;
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось распарсить uuid");
        }

        try {
            Message request = new Message(new LegalAccountEntity(uuid));
            jmsTemplate.convertAndSend(queueName + "GetByIdRequest", request);
            Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByIdResponse");
            legalAccountEntity = response.getLegalAccount();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }
        if (legalAccountEntity == null)
            throw new IllegalArgumentException("Не найден счёт с данным id");
        return legalAccountEntity;
    }

    public LegalAccountDTO getAccountById(@Nonnull String id) {
        return new LegalAccountDTO(this.getAccountEntityById(id));
    }
}
