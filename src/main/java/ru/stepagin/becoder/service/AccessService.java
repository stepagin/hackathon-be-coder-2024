package ru.stepagin.becoder.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {
    private final AccessRepository accessRepository;
    private final PersonRepository personRepository;

    public void save(AccessEntity access) {
        accessRepository.save(access);
    }

    public boolean checkHasAccess(Long personId, UUID accountId) {
        AccessEntity access = accessRepository.findByAccount_IdAndPersonId(accountId, personId);
        return access != null;
    }

    public List<LegalAccountDTO> getAllByPerson(PersonEntity person) {
        return accessRepository.findByPerson(person).stream().map(LegalAccountDTO::new).toList();
    }

    @Transactional
    public void grantAccess(LegalAccountEntity account, String login) {
        // TODO
        PersonEntity person = personRepository.findByLogin(login);
        if (person == null || account == null)
            throw new IllegalArgumentException("Некорректный запрос на выдачу прав: отсутствуют необходимые данные.");
        accessRepository.save(new AccessEntity(person, account));
    }

    @Transactional
    public void revokeAccess(LegalAccountEntity account, String login) {
        // TODO
        PersonEntity person = personRepository.findByLogin(login);
        if (person == null || account == null)
            throw new IllegalArgumentException("Некорректный запрос на отзыв прав: отсутствуют необходимые данные.");
        AccessEntity access = accessRepository.findByAccount_IdAndPersonId(account.getId(), person.getId());
        accessRepository.delete(access);
    }
}
