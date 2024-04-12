package ru.stepagin.becoder.service;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

import java.util.Objects;
import java.util.UUID;

@Service
@Component
@EnableJms
public class SecurityService {
//    private final JmsTemplate jmsTemplate;
    final AccessService accessService;
    final PersonService personService;

//    private final String queueName = "Person";

    public SecurityService(AccessService accessService, PersonService personService) {
        this.accessService = accessService;
        this.personService = personService;

    }

//    public boolean isAuthorized(Long id, Authentication authentication) {
//        UserDetails user = (UserDetails) authentication.getPrincipal();
//        Message request = new Message(new PersonEntity(user.getUsername()));
//        jmsTemplate.convertAndSend(queueName + "GetByLoginRequest", request);
//        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByLoginResponse");
//        PersonEntity person = response.getPerson();
//        if (person == null) return false;
//        return person.getId() != null && person.getId().equals(id);
//    }


    public PersonEntity getPerson(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return personService.getPersonByLogin(user.getUsername());
    }

    public boolean checkIdIsSame(Long id, Authentication authentication){
        PersonEntity person = getPerson(authentication);
        if (person == null) return false;
        return Objects.equals(person.getId(), id);
    }

    public boolean hasAccessToAccount(String accountId, Authentication authentication) {
        PersonEntity person = getPerson(authentication);
        if (person == null) return false;
        return accessService.checkHasAccess(person.getId(), UUID.fromString(accountId));
    }

    public boolean hasAccessToAccount(BalanceChangeDTO balanceChangeDTO, Authentication authentication){
        return hasAccessToAccount(balanceChangeDTO.getAccount().getId(), authentication);
    }

}
