package ru.stepagin.becoder.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.dto.RegistrationDto;
import ru.stepagin.becoder.service.PersonService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private PersonService personService;

    private AuthController authController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        authController = new AuthController(personService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void register() {
        RegistrationDto registrationDto = new RegistrationDto("user", "user");
        PersonDto person = new PersonDto();
        person.setLogin(registrationDto.getLogin());

        when(personService.registerPerson(registrationDto.getLogin(), registrationDto.getPassword())).thenReturn(person);

        ResponseEntity<PersonDto> response = authController.register(registrationDto);

        verify(personService, times(1)).registerPerson(registrationDto.getLogin(), registrationDto.getPassword());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(person.getLogin(), response.getBody().getLogin());
    }

    @Test
    void getAll() {
        List<PersonDto> list = new ArrayList<>();

        when(personService.getAllUsers()).thenReturn(list);

        ResponseEntity<List<PersonDto>> response = authController.getAll();

        verify(personService, times(1)).getAllUsers();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(list, response.getBody());
    }
}