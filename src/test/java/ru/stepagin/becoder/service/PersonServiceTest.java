package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @Mock
    private PersonRepository personRepository;

    private PersonService personService;
    private AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        personService = new PersonService(personRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerPerson() {
        String login = "user123123";
        String password = "user123123";

        PersonEntity person = new PersonEntity(login, password);

        personService.registerPerson(login, password);

        ArgumentCaptor<PersonEntity> personEntityArgumentCaptor = ArgumentCaptor.forClass(PersonEntity.class);
        verify(personRepository).save(personEntityArgumentCaptor.capture());

        PersonEntity result = personEntityArgumentCaptor.getValue();

        assertEquals(person.getLogin(), result.getLogin());
    }

    @Test
    void registerNullPerson() {
        String login = null;
        String password = null;
        assertThrows(IllegalArgumentException.class, () -> personService.registerPerson(login, password));

    }


    @Test
    void registerExistingPerson() {
        String login = "qwerty1";
        String password = "qwerty1";
        PersonEntity person = new PersonEntity(login, password);
        when(personRepository.existsByLoginAllIgnoreCase(login)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> personService.registerPerson(login, password));
    }

    @Test
    void getAllUsers() {
        personService.getAllUsers();
        verify(personRepository, times(1)).findAll();
    }
}