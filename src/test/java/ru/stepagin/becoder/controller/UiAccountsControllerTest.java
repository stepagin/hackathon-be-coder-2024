package ru.stepagin.becoder.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import ru.stepagin.becoder.dto.BalanceChangeDto;
import ru.stepagin.becoder.dto.BalanceChangeUiDto;
import ru.stepagin.becoder.dto.LegalAccountDto;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.PersonService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UiAccountsControllerTest {


    @Mock
    private BalanceController balanceController;
    @Mock
    private PersonService personService;
    @Mock
    private SecurityService securityService;
    @Mock
    private AccessService accessService;
    @Mock
    private LegalAccountService legalAccountService;
    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;

    private UiAccountsController uiAccountsController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        uiAccountsController = new UiAccountsController(balanceController, personService, securityService, accessService, legalAccountService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void getAll() {
        List<LegalAccountDto> accounts = new ArrayList<>();

        ResponseEntity<List<LegalAccountDto>> response = ResponseEntity.ok().body(accounts);
        PersonEntity person = new PersonEntity();

        when(balanceController.getAllAccounts(authentication)).thenReturn(response);
        when(securityService.getPerson(authentication)).thenReturn(person);

        uiAccountsController.getAll(authentication, model);

        verify(balanceController, times(1)).getAllAccounts(authentication);
        verify(securityService, times(1)).getPerson(authentication);


    }

    @Test
    void getAccountDetails() {
    }

    @Test
    void createAccount() {
        LegalAccountDto s = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.ok(s);
        when(balanceController.createAccount(authentication)).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String res = uiAccountsController.createAccount(authentication, request, model);


        verify(balanceController, times(1)).createAccount(authentication);

        assertTrue(res.startsWith("redirect"));
    }

    @Test
    void createAccountError() {
        LegalAccountDto s = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.status(500).body(s);
        when(balanceController.createAccount(authentication)).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String res = uiAccountsController.createAccount(authentication, request, model);


        verify(balanceController, times(1)).createAccount(authentication);

        assertTrue(res.startsWith("error"));
    }

    @Test
    void increaseAccountBalance() {
        BalanceChangeUiDto balanceChangeUiDto = new BalanceChangeUiDto();
        balanceChangeUiDto.setAmount(100D);


        String uuid = UUID.randomUUID().toString();


        LegalAccountDto legalAccountDto = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.ok(legalAccountDto);

        when(balanceController.increaseAccountBalance(eq(uuid), any(BalanceChangeDto.class))).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String result = uiAccountsController.increaseAccountBalance(uuid, balanceChangeUiDto, request, model);

        verify(balanceController, times(1)).increaseAccountBalance(eq(uuid), any(BalanceChangeDto.class));
        assertTrue(result.startsWith("redirect"));

    }

    @Test
    void increaseAccountBalanceError() {
        BalanceChangeUiDto balanceChangeUiDto = new BalanceChangeUiDto();
        balanceChangeUiDto.setAmount(100D);


        String uuid = UUID.randomUUID().toString();


        LegalAccountDto legalAccountDto = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.internalServerError().body(legalAccountDto);

        when(balanceController.increaseAccountBalance(eq(uuid), any(BalanceChangeDto.class))).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String result = uiAccountsController.increaseAccountBalance(uuid, balanceChangeUiDto, request, model);

        verify(balanceController, times(1)).increaseAccountBalance(eq(uuid), any(BalanceChangeDto.class));
        assertTrue(result.startsWith("error"));

    }

    @Test
    void decreaseAccountBalance() {
        BalanceChangeUiDto balanceChangeUiDto = new BalanceChangeUiDto();
        balanceChangeUiDto.setAmount(100D);

        String uuid = UUID.randomUUID().toString();


        LegalAccountDto legalAccountDto = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.ok(legalAccountDto);

        when(balanceController.decreaseAccountBalance(eq(uuid), any(BalanceChangeDto.class))).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String result = uiAccountsController.decreaseAccountBalance(uuid, balanceChangeUiDto, request, model);

        verify(balanceController, times(1)).decreaseAccountBalance(eq(uuid), any(BalanceChangeDto.class));
        assertTrue(result.startsWith("redirect"));

    }


    @Test
    void decreaseAccountBalanceError() {
        BalanceChangeUiDto balanceChangeUiDto = new BalanceChangeUiDto();
        balanceChangeUiDto.setAmount(100D);

        String uuid = UUID.randomUUID().toString();


        LegalAccountDto legalAccountDto = new LegalAccountDto();
        ResponseEntity<?> response = ResponseEntity.internalServerError().body(legalAccountDto);

        when(balanceController.decreaseAccountBalance(eq(uuid), any(BalanceChangeDto.class))).thenReturn((ResponseEntity<LegalAccountDto>) response);

        String result = uiAccountsController.decreaseAccountBalance(uuid, balanceChangeUiDto, request, model);

        verify(balanceController, times(1)).decreaseAccountBalance(eq(uuid), any(BalanceChangeDto.class));
        assertTrue(result.startsWith("error"));

    }

    @Test
    void grantAccessToAccount() {
        PersonDto personDto = new PersonDto();
        String uuid = UUID.randomUUID().toString();

        ResponseEntity<?> response = ResponseEntity.ok("ok");

        when(balanceController.grantAccessToAccount(uuid, personDto)).thenReturn((ResponseEntity<String>) response);

        String result = uiAccountsController.grantAccessToAccount(uuid, personDto, request, model);

        verify(balanceController, times(1)).grantAccessToAccount(uuid, personDto);
        assertTrue(result.startsWith("redirect"));
    }


    @Test
    void grantAccessToAccountError() {
        PersonDto personDto = new PersonDto();
        String uuid = UUID.randomUUID().toString();

        ResponseEntity<?> response = ResponseEntity.internalServerError().body("error");

        when(balanceController.grantAccessToAccount(uuid, personDto)).thenReturn((ResponseEntity<String>) response);

        String result = uiAccountsController.grantAccessToAccount(uuid, personDto, request, model);

        verify(balanceController, times(1)).grantAccessToAccount(uuid, personDto);
        assertTrue(result.startsWith("error"));
    }

    @Test
    void revokeAccessFromAccount() {
        PersonDto personDto = new PersonDto();
        personDto.setLogin("user");
        String uuid = UUID.randomUUID().toString();

        ResponseEntity<?> response = ResponseEntity.ok("ok");

        when(balanceController.revokeAccessFromAccount(uuid, personDto.getLogin())).thenReturn((ResponseEntity<String>) response);

        String result = uiAccountsController.revokeAccessFromAccount(uuid, personDto, request, model);

        verify(balanceController, times(1)).revokeAccessFromAccount(uuid, personDto.getLogin());
        assertTrue(result.startsWith("redirect"));
    }


    @Test
    void revokeAccessFromAccountError() {
        PersonDto personDto = new PersonDto();
        personDto.setLogin("user");
        String uuid = UUID.randomUUID().toString();

        ResponseEntity<?> response = ResponseEntity.internalServerError().body("error");

        when(balanceController.revokeAccessFromAccount(uuid, personDto.getLogin())).thenReturn((ResponseEntity<String>) response);

        String result = uiAccountsController.revokeAccessFromAccount(uuid, personDto, request, model);

        verify(balanceController, times(1)).revokeAccessFromAccount(uuid, personDto.getLogin());
        assertTrue(result.startsWith("error"));
    }
}