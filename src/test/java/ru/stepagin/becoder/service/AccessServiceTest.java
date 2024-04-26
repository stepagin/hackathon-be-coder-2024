package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {
    @Mock
    private AccessRepository accessRepository;
    @Mock
    private PersonRepository personRepository;
    private AccessService accessService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        accessService = new AccessService(accessRepository, personRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void save() {
        PersonEntity person = new PersonEntity("user", "user");
        LegalAccountEntity account = new LegalAccountEntity(person);
        account.setId(UUID.randomUUID());
        AccessEntity access = new AccessEntity(person, account);

        accessService.save(access);

        ArgumentCaptor<AccessEntity> accessEntityArgumentCaptor = ArgumentCaptor.forClass(AccessEntity.class);
        verify(accessRepository).save(accessEntityArgumentCaptor.capture());

        AccessEntity result = accessEntityArgumentCaptor.getValue();

        assertEquals(access, result);
    }

    @Test
    void checkHasAccess() {
        accessService.checkHasAccess(1L, UUID.randomUUID());
        verify(accessRepository, times(1)).findByAccountIdAndPersonId(any(UUID.class), any(Long.class));
    }

    @Test
    void getAllByPerson() {
        PersonEntity person = new PersonEntity("user", "user");
        accessService.getAllByPerson(person);
        verify(accessRepository, times(1)).findByPerson(any(PersonEntity.class));
    }

    @Test
    void grantAccess() {
        PersonEntity personEntity = new PersonEntity("user", "user");
        LegalAccountEntity account = new LegalAccountEntity(personEntity);

        when(personRepository.findByLogin(personEntity.getLogin())).thenReturn(personEntity);
        accessService.grantAccess(account, personEntity.getLogin());

        verify(personRepository, times(1)).findByLogin(personEntity.getLogin());
        verify(accessRepository, times(1)).save(any(AccessEntity.class));

    }

    @Test
    void revokeAccess() {
        PersonEntity person = new PersonEntity("user", "user");
        Long personId = 1L;
        person.setId(personId);

        LegalAccountEntity account = new LegalAccountEntity(person);
        UUID uuid = UUID.randomUUID();
        account.setId(uuid);

        AccessEntity access = new AccessEntity(person, account);

        when(personRepository.findByLogin(person.getLogin())).thenReturn(person);
        when(accessRepository.findByAccountIdAndPersonId(uuid, personId)).thenReturn(access);

        accessService.revokeAccess(account, person.getLogin());

        verify(personRepository, times(1)).findByLogin(any(String.class));
        verify(accessRepository, times(1)).findByAccountIdAndPersonId(any(UUID.class), any(Long.class));
        verify(accessRepository, times(1)).delete(any(AccessEntity.class));

    }
}