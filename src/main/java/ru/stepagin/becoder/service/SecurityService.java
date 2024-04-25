package ru.stepagin.becoder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final PersonRepository personRepository;
    private final AccessService accessService;
    private final LegalAccountService legalAccountService;

    public PersonEntity getPerson(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return personRepository.findByLogin(user.getUsername());
    }

    public boolean isActiveOwner(String accountId, Authentication authentication) {
        PersonEntity person = getPerson(authentication);
        if (person == null) return false;
        return legalAccountService.isActiveOwner(person, UUID.fromString(accountId));
    }

    public boolean hasAccessToAccount(String accountId, Authentication authentication) {
        PersonEntity person = getPerson(authentication);
        if (person == null) return false;
        return accessService.checkHasAccess(person.getId(), UUID.fromString(accountId));
    }

    public boolean hasAccessToAccount(BalanceChangeDTO balanceChangeDTO, Authentication authentication) {
        if (balanceChangeDTO.getAccount() == null || balanceChangeDTO.getAccount().getId() == null) {
            throw new IllegalArgumentException("Не переданы данные счёта");
        }
        return hasAccessToAccount(balanceChangeDTO.getAccount().getId(), authentication);
    }

}
