package ru.stepagin.becoder.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccessService {
    private final AccessRepository accessRepository;
    private final PersonService personService;
    private final PersonRepository personRepository;
    private final LegalAccountService legalAccountService;

    public AccessService(AccessRepository accessRepository, PersonService personService, PersonRepository personRepository, LegalAccountService legalAccountService) {
        this.accessRepository = accessRepository;
        this.personService = personService;
        this.personRepository = personRepository;
        this.legalAccountService = legalAccountService;
    }

    public void save(AccessEntity access){
        accessRepository.save(access);
    }

    public boolean checkHasAccess(Long personId, UUID accoutId) {
        AccessEntity access;
        try {
            access = accessRepository.findByAccount_IdAndPersonId(accoutId, personId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
        return access != null;
    }

    public List<AccessDTO> getAllByUserId(Long id){
        return accessRepository.findAll()
                .stream()
                .filter(x -> x.getPerson().getId().equals(id))
                .map(x -> new AccessDTO(x.getAccount().getId().toString()))
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean grantAccess(String accountId, String login){
        PersonEntity person = personRepository.findByLogin(login);
        if(person == null) return false;
        LegalAccountEntity account = legalAccountService.getAccountEntityById(accountId);
        if(account == null) return false;
        accessRepository.save(new AccessEntity(person, account));
        return true;
    }

    @Transactional
    public boolean revokeAccess(String accountId, String login){
        PersonEntity person = personRepository.findByLogin(login);
        if(person == null) return false;
        LegalAccountEntity account = legalAccountService.getAccountEntityById(accountId);
        if(account == null) return false;
        accessRepository.delete(new AccessEntity(person, account));
        return true;
    }
}
