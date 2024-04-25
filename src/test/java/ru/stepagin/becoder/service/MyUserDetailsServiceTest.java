package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MyUserDetailsServiceTest {

    private MyUserDetailsService userDetailsService;
    @Mock
    private PersonRepository personRepository;

    private AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userDetailsService = new MyUserDetailsService(personRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void loadUserByUsername() {
        PersonEntity person = new PersonEntity("user", "user");

        when(personRepository.findByLogin(any(String.class))).thenReturn(person);

        UserDetails userDetails = userDetailsService.loadUserByUsername(person.getLogin());

        verify(personRepository, times(1)).findByLogin(any(String.class));

        assertEquals(userDetails.getUsername(), person.getLogin());
        assertEquals(userDetails.getPassword(), person.getPassword());
    }

    @Test
    void loadUserByUsernameWhenNotFound() {
        String login = "null";

        when(personRepository.findByLogin(any(String.class))).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () ->userDetailsService.loadUserByUsername(login));

        verify(personRepository, times(1)).findByLogin(any(String.class));

    }
}