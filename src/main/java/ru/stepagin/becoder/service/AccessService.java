package ru.stepagin.becoder.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AccessService {
    private final AccessRepository accessRepository;
    private final PersonRepository personRepository;

    public AccessService(AccessRepository accessRepository, PersonRepository personRepository) {
        this.accessRepository = accessRepository;
        this.personRepository = personRepository;
    }

    public void save(AccessEntity access) {
        accessRepository.save(access);
    }


    public boolean checkHasAccess(Long personId, UUID accountId) {
        AccessEntity access = accessRepository.findByAccount_IdAndPersonId(accountId, personId);
        return access != null;
    }

    public List<AccessDTO> getAllByPerson(PersonEntity person) {
        return accessRepository.findByPerson(person).stream().map(AccessDTO::new).toList();
    }

    @Transactional
    public boolean grantAccess(LegalAccountEntity account, String login) {
        PersonEntity person = personRepository.findByLogin(login);
        if (person == null || account == null) return false;
        accessRepository.save(new AccessEntity(person, account));
        return true;
    }

    @Transactional
    public boolean revokeAccess(LegalAccountEntity account, String login) {
        PersonEntity person = personRepository.findByLogin(login);
        if (person == null || account == null) return false;
        accessRepository.delete(accessRepository.findByAccount_IdAndPersonId(account.getId(), person.getId()));
        return true;
    }
}
