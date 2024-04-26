package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityServiceTest {

    @Mock
    private Authentication authentication;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private AccessService accessService;
    @Mock
    private LegalAccountService legalAccountService;
    @Mock
    private UserDetails userDetails;

    private SecurityService securityService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        securityService = new SecurityService(personRepository, accessService, legalAccountService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getPerson() {
        String login = "login";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(login);

        securityService.getPerson(authentication);

        verify(personRepository, times(1)).findByLogin(login);
    }

    @Test
    void isActiveOwner() {
        UUID uuid = UUID.randomUUID();
        String login = "login";

        PersonEntity person = new PersonEntity(login, "password");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(login);
        when(personRepository.findByLogin(login)).thenReturn(person);
        when(legalAccountService.isActiveOwner(person, uuid)).thenReturn(true);

        securityService.isActiveOwner(uuid.toString(), authentication);

        verify(personRepository, times(1)).findByLogin(login);
        verify(legalAccountService, times(1)).isActiveOwner(person, uuid);
    }

    @Test
    void hasAccessToAccount() {
        UUID uuid = UUID.randomUUID();
        String login = "login";

        PersonEntity person = new PersonEntity(login, "password");
        person.setId(1L);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(login);
        when(personRepository.findByLogin(login)).thenReturn(person);
        when(accessService.checkHasAccess(person.getId(), uuid)).thenReturn(true);

        securityService.hasAccessToAccount(uuid.toString(), authentication);

        verify(personRepository, times(1)).findByLogin(login);
        verify(accessService, times(1)).checkHasAccess(person.getId(), uuid);
    }

    @Test
    void hasNotAccessToAccount() {
        UUID uuid = UUID.randomUUID();
        String login = "login";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(login);
        when(personRepository.findByLogin(login)).thenReturn(null);

        boolean res = securityService.hasAccessToAccount(uuid.toString(), authentication);

        verify(personRepository, times(1)).findByLogin(login);

        assertFalse(res);
    }
}