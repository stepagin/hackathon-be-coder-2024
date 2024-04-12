package ru.stepagin.becoder.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.Objects;
import java.util.UUID;

@Service
public class SecurityService {
    final PersonRepository personRepository;
    final AccessService accessService;

    public SecurityService(PersonRepository personRepository, AccessService accessService) {
        this.personRepository = personRepository;
        this.accessService = accessService;
    }

    public boolean checkIdIsSame(Long id, Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        PersonEntity person = personRepository.findByLogin(user.getUsername());
        if (person == null) return false;
        return Objects.equals(person.getId(), id);
    }

    public boolean hasAccessToAccount(String accountId, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        PersonEntity person = personRepository.findByLogin(user.getUsername());
        if (person == null) return false;
        return accessService.checkHasAccess(person.getId(), UUID.fromString(accountId));
    }

    public boolean hasAccessToAccount(BalanceChangeDTO balanceChangeDTO, Authentication authentication){
        return hasAccessToAccount(balanceChangeDTO.getAccount().getId(), authentication);
    }

}
