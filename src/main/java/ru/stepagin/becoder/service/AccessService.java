package ru.stepagin.becoder.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.AccessRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {
    private final AccessRepository accessRepository;
    private final LegalAccountService legalAccountService;
    private final PersonService personService;

    public void save(AccessEntity access) {
        accessRepository.save(access);
    }

    public boolean checkHasAccess(Long personId, UUID accountId) {
        AccessEntity access = accessRepository.findByAccountIdAndPersonId(accountId, personId);
        return access != null;
    }

    public List<LegalAccountDTO> getAllByPerson(PersonEntity person) {
        return accessRepository.findByPerson(person).stream().map(LegalAccountDTO::new).toList();
    }

    @Transactional
    public void grantAccess(String accountId, String personLogin) {
        AccessEntity accessEntity = accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(UUID.fromString(accountId), personLogin);
        if (accessEntity != null)
            throw new IllegalArgumentException("У пользователя уже есть доступ к счёту");

        LegalAccountEntity account = legalAccountService.getAccountEntityById(accountId);
        if (account == null)
            throw new InvalidIdSuppliedException("Не существует счёта с данным id");

        PersonEntity person = personService.getPersonEntity(personLogin);
        if (person == null)
            throw new InvalidIdSuppliedException("Не существует пользователя с данным login");

        accessRepository.save(new AccessEntity(person, account));
    }

    @Transactional
    public void revokeAccess(String accountId, String personLogin) {
        AccessEntity accessEntity = accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(UUID.fromString(accountId), personLogin);
        if (accessEntity == null)
            throw new IllegalArgumentException("У пользователя нет доступа к счёту");
        if (Objects.equals(accessEntity.getPerson().getLogin(), accessEntity.getAccount().getCreator().getLogin()))
            throw new InvalidIdSuppliedException("Невозможно отозвать права у владельца счёта");

        accessRepository.delete(accessEntity);
    }

    public List<PersonDTO> getPartnersByAccountId(String accountId) {
        return accessRepository.findByAccountId(UUID.fromString(accountId)).stream()
                .map((accessEntity) -> new PersonDTO(accessEntity.getPerson())).toList();
    }
}
