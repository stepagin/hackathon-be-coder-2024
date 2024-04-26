package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.AccessRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {
    @Mock
    private AccessRepository accessRepository;
    @Mock
    private LegalAccountService legalAccountService;
    @Mock
    private PersonService personService;

    private AccessService accessService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        accessService = new AccessService(accessRepository, legalAccountService, personService);
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
        UUID uuid = UUID.randomUUID();
        String personLogin = "user";

        LegalAccountEntity account = new LegalAccountEntity();
        PersonEntity person = new PersonEntity();

        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin)).thenReturn(null);
        when(legalAccountService.getAccountEntityById(uuid.toString())).thenReturn(account);
        when(personService.getPersonEntity(personLogin)).thenReturn(person);

        accessService.grantAccess(uuid.toString(), personLogin);


        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin);
        verify(legalAccountService, times(1)).getAccountEntityById(uuid.toString());
        verify(personService, times(1)).getPersonEntity(personLogin);
        verify(accessRepository, times(1)).save(any(AccessEntity.class));
    }

    @Test
    void grantExistingAccess() {
        UUID uuid = UUID.randomUUID();
        String personLogin = "user";

        LegalAccountEntity account = new LegalAccountEntity();
        PersonEntity person = new PersonEntity();
        AccessEntity access = new AccessEntity(person, account);
        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin)).thenReturn(access);

        assertThrows(IllegalArgumentException.class, () -> accessService.grantAccess(uuid.toString(), personLogin));


        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin);
    }


    @Test
    void grantAccessToNotExistingAccount() {
        UUID uuid = UUID.randomUUID();
        String personLogin = "user";


        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin)).thenReturn(null);
        when(legalAccountService.getAccountEntityById(uuid.toString())).thenReturn(null);

        assertThrows(InvalidIdSuppliedException.class, () -> accessService.grantAccess(uuid.toString(), personLogin));


        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin);
        verify(legalAccountService, times(1)).getAccountEntityById(uuid.toString());
    }

    @Test
    void grantAccessToNotExistingPerson() {
        UUID uuid = UUID.randomUUID();
        String personLogin = "user";

        LegalAccountEntity account = new LegalAccountEntity();

        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin)).thenReturn(null);
        when(legalAccountService.getAccountEntityById(uuid.toString())).thenReturn(account);
        when(personService.getPersonEntity(personLogin)).thenReturn(null);

        assertThrows(InvalidIdSuppliedException.class, () -> accessService.grantAccess(uuid.toString(), personLogin));


        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, personLogin);
        verify(legalAccountService, times(1)).getAccountEntityById(uuid.toString());
        verify(personService, times(1)).getPersonEntity(personLogin);
    }

    @Test
    void revokeAccess() {
        UUID uuid = UUID.randomUUID();

        PersonEntity person = new PersonEntity("user", "user");
        LegalAccountEntity account = new LegalAccountEntity(person);
        account.setId(uuid);

        PersonEntity not_owner = new PersonEntity("not_owner", "user");

        AccessEntity access = new AccessEntity(not_owner, account);

        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, "not_owner")).thenReturn(access);

        accessService.revokeAccess(uuid.toString(), "not_owner");


        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, "not_owner");
        verify(accessRepository, times(1)).delete(any(AccessEntity.class));

    }


    @Test
    void revokeAccessFromOwner() {
        UUID uuid = UUID.randomUUID();

        PersonEntity person = new PersonEntity("user", "user");
        LegalAccountEntity account = new LegalAccountEntity(person);
        account.setId(uuid);


        AccessEntity access = new AccessEntity(person, account);

        when(accessRepository.findByAccount_IdAndPerson_LoginIgnoreCase(uuid, "user")).thenReturn(access);

        assertThrows(InvalidIdSuppliedException.class, () -> accessService.revokeAccess(uuid.toString(), "user"));

        verify(accessRepository, times(1)).findByAccount_IdAndPerson_LoginIgnoreCase(uuid, "user");

    }

    @Test
    void getPartnersByAccountId() {
        String uuid = UUID.randomUUID().toString();
        List<AccessEntity> list = new ArrayList<>();

        when(accessRepository.findByAccountId(UUID.fromString(uuid))).thenReturn(list);

        accessService.getPartnersByAccountId(uuid);

        verify(accessRepository, times(1)).findByAccountId(UUID.fromString(uuid));

    }

}