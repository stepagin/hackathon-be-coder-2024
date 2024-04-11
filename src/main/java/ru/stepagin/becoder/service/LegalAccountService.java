package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.UUID;

@Slf4j
@Service
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
    @JmsListener(destination = queueName+"Save")
    public LegalAccountDTO createAccount() {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(0L);
        Message message = new Message(legalAccountEntity);
        Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
//        legalAccountEntity = legalAccountRepository.save(legalAccountEntity);
        assert response != null;
        return new LegalAccountDTO(response.getLegalAccount());
    }


    @Transactional
    public void decreaseBalance(@Nonnull BalanceChangeDTO balanceChange) {
        // check user has access
        if (!accessService.checkHasAccess(balanceChange.getPerson().getId(),
                UUID.fromString(balanceChange.getAccount().getId())))
            throw new IllegalArgumentException("У данного пользователя нет доступа к этому счёту");

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
        jmsTemplate.convertAndSend(queueName+"Update", message);
//        legalAccountRepository.updateBalanceByAccountId(
//                accountId,
//                balance
//        );

    }


    @JmsListener(destination = queueName+"GetById")
    public LegalAccountEntity getAccountEntityById(@Nonnull String id) {
        LegalAccountEntity legalAccountEntity;
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось распарсить uuid");
        }

        try {
//            legalAccountEntity = legalAccountRepository.findById(uuid).orElse(null);
            Message message = new Message(new LegalAccountEntity(uuid));
            Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
            assert response != null;
            legalAccountEntity = response.getLegalAccount();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе поиска данных счёта");
        }

        if (legalAccountEntity == null) {
            throw new IllegalArgumentException("Не найден счёт с данным id");
        }
        return legalAccountEntity;
    }

    public LegalAccountDTO getAccountById(@Nonnull String id) {
        return new LegalAccountDTO(this.getAccountEntityById(id));
    }
}
