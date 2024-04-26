package ru.stepagin.becoder.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.stepagin.becoder.dto.BalanceChangeDto;
import ru.stepagin.becoder.dto.LegalAccountDto;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.PersonService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class BalanceControllerTest {

    @Mock
    private LegalAccountService accountService;

    @Mock
    private AccessService accessService;

    @Mock
    private SecurityService securityService;

    @Mock
    private PersonService personService;

    @Mock
    private Authentication authentication;

    private BalanceController balanceController;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        balanceController = new BalanceController(accountService, accessService, securityService, personService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllAccounts() {
        PersonEntity person = new PersonEntity();

        List<LegalAccountDto> list = new ArrayList<>();
        LegalAccountDto legalAccountDto = new LegalAccountDto();
        legalAccountDto.setId(UUID.randomUUID().toString());
        legalAccountDto.setBalance(100);

        list.add(legalAccountDto);

        when(securityService.getPerson(authentication)).thenReturn(person);
        when(accessService.getAllByPerson(person)).thenReturn(list);

        ResponseEntity<List<LegalAccountDto>> response = balanceController.getAllAccounts(authentication);

        verify(securityService, times(1)).getPerson(authentication);
        verify(accessService, times(1)).getAllByPerson(person);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getAccountDetails() {
        String uuid = UUID.randomUUID().toString();
        LegalAccountDto legalAccountDTO = new LegalAccountDto();
        legalAccountDTO.setId(uuid);
        legalAccountDTO.setBalance(100);
        when(accountService.getAccountById(uuid)).thenReturn(legalAccountDTO);

        ResponseEntity<LegalAccountDto> response = balanceController.getAccountDetails(uuid);

        verify(accountService, times(1)).getAccountById(uuid);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(legalAccountDTO, response.getBody());
    }

    @Test
    void createAccount() {
        PersonEntity person = new PersonEntity();
        LegalAccountDto legalAccountDTO = new LegalAccountDto();
        when(securityService.getPerson(authentication)).thenReturn(person);
        when(accountService.createAccount(person)).thenReturn(legalAccountDTO);

        ResponseEntity<LegalAccountDto> response = balanceController.createAccount(authentication);

        verify(securityService, times(1)).getPerson(authentication);
        verify(accountService, times(1)).createAccount(person);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(legalAccountDTO, response.getBody());
    }


    @Test
    void increaseAccountBalance() {
        String uuid = UUID.randomUUID().toString();
        BalanceChangeDto balanceChangeDTO = new BalanceChangeDto();
        balanceChangeDTO.setAmount(100D);

        LegalAccountEntity account = new LegalAccountEntity();
        account.setBalance(0L);
        account.setId(UUID.fromString(uuid));

        when(accountService.getAccountEntityById(uuid)).thenReturn(account);

        ResponseEntity<LegalAccountDto> response = balanceController.increaseAccountBalance(uuid, balanceChangeDTO);

        verify(accountService, times(1)).increaseBalance(account, balanceChangeDTO.getAmount());

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        LegalAccountDto legalAccountDto = new LegalAccountDto();
        legalAccountDto.setBalance(account.getBalance());

        assertEquals(legalAccountDto.getBalance(), response.getBody().getBalance());

    }

    @Test
    void decreaseAccountBalance() {

        String uuid = UUID.randomUUID().toString();
        BalanceChangeDto balanceChangeDTO = new BalanceChangeDto();
        balanceChangeDTO.setAmount(100D);

        LegalAccountEntity account = new LegalAccountEntity();
        account.setBalance(0L);
        account.setId(UUID.fromString(uuid));

        when(accountService.isEnough(uuid, balanceChangeDTO.getAmount())).thenReturn(account);

        ResponseEntity<LegalAccountDto> response = balanceController.decreaseAccountBalance(uuid, balanceChangeDTO);

        verify(accountService, times(1)).decreaseBalance(account, balanceChangeDTO.getAmount());

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        LegalAccountDto legalAccountDto = new LegalAccountDto();
        legalAccountDto.setBalance(account.getBalance());

        assertEquals(legalAccountDto.getBalance(), response.getBody().getBalance());

    }


    @Test
    void getAllPartners() {
        String accountId = UUID.randomUUID().toString();
        List<PersonDto> list = new ArrayList<>();

        when(accessService.getPartnersByAccountId(accountId)).thenReturn(list);

        ResponseEntity<List<PersonDto>> response = balanceController.getAllPartners(accountId);

        verify(accessService, times(1)).getPartnersByAccountId(accountId);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(list, response.getBody());

    }

    @Test
    void grantAccessToAccount() {
        PersonEntity person = new PersonEntity("user", "user");
        String accountId = UUID.randomUUID().toString();
        PersonDto personDto = new PersonDto();
        personDto.setLogin(person.getLogin());
        ResponseEntity<String> response = balanceController.grantAccessToAccount(accountId, personDto);

        verify(accessService, times(1)).grantAccess(accountId, person.getLogin());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

    }

    @Test
    void revokeAccessFromAccount() {
        PersonEntity person = new PersonEntity("user", "user");
        String accountId = UUID.randomUUID().toString();

        PersonDto personDto = new PersonDto();
        personDto.setLogin(person.getLogin());
        when(personService.getUser(person.getLogin())).thenReturn(personDto);

        ResponseEntity<String> response = balanceController.revokeAccessFromAccount(accountId, person.getLogin());

        verify(accessService, times(1)).revokeAccess(accountId, person.getLogin());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
}