//package ru.stepagin.becoder.controller;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import ru.stepagin.becoder.DTO.BalanceChangeDto;
//import ru.stepagin.becoder.DTO.LegalAccountDto;
//import ru.stepagin.becoder.entity.PersonEntity;
//import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
//import ru.stepagin.becoder.service.AccessService;
//import ru.stepagin.becoder.service.LegalAccountService;
//import ru.stepagin.becoder.service.SecurityService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//class BalanceControllerTest {
//
//    @Mock
//    private LegalAccountService accountService;
//
//    @Mock
//    private AccessService accessService;
//
//    @Mock
//    private SecurityService securityService;
//
//    @Mock
//    private Authentication authentication;
//
//    private BalanceController balanceController;
//    private AutoCloseable autoCloseable;
//
//    @BeforeEach
//    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
//        balanceController = new BalanceController(accountService, accessService, securityService);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }
//
//    @Test
//    void getAllAccounts() {
//        PersonEntity person = new PersonEntity();
//
//        List<LegalAccountDto> list = new ArrayList<>();
//        list.add(new LegalAccountDto(UUID.randomUUID().toString(), 100));
//        list.add(new LegalAccountDto(UUID.randomUUID().toString(), 100));
//
//        when(securityService.getPerson(authentication)).thenReturn(person);
//        when(accessService.getAllByPerson(person)).thenReturn(list);
//
//        ResponseEntity<List<LegalAccountDto>> response = balanceController.getAllAccounts(authentication);
//
//        verify(securityService, times(1)).getPerson(authentication);
//        verify(accessService, times(1)).getAllByPerson(person);
//
//        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
//        assertEquals(list, response.getBody());
//    }
//
//    @Test
//    void getAccountDetails() {
//        String uuid = UUID.randomUUID().toString();
//        LegalAccountDto legalAccountDTO = new LegalAccountDto(uuid, 100);
//        when(accountService.getAccountById(uuid)).thenReturn(legalAccountDTO);
//
//        ResponseEntity<LegalAccountDto> response = balanceController.getAccountDetails(uuid);
//
//        verify(accountService, times(1)).getAccountById(uuid);
//
//        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
//        assertEquals(legalAccountDTO, response.getBody());
//    }
//
//    @Test
//    void createAccount() {
//        PersonEntity person = new PersonEntity();
//        LegalAccountDto legalAccountDTO = new LegalAccountDto();
//        when(securityService.getPerson(authentication)).thenReturn(person);
//        when(accountService.createAccount(person)).thenReturn(legalAccountDTO);
//
//        ResponseEntity<LegalAccountDto> response = balanceController.createAccount(authentication);
//
//        verify(securityService, times(1)).getPerson(authentication);
//        verify(accountService, times(1)).createAccount(person);
//
//        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
//        assertEquals(legalAccountDTO, response.getBody());
//    }
//
//    @Test
//    void createAccountForNullPerson() {
//        when(securityService.getPerson(authentication)).thenReturn(null);
//
//        assertThrows(InvalidIdSuppliedException.class, () -> balanceController.createAccount(authentication));
//
//        verify(securityService, times(1)).getPerson(authentication);
//
//    }
//
//    @Test
//    void increaseAccountBalance() {
//        String uuid = UUID.randomUUID().toString();
//        BalanceChangeDto balanceChangeDTO = new BalanceChangeDto();
//        balanceChangeDTO.setAmount(100D);
//
//        LegalAccountDto legalAccountDTO = new LegalAccountDto(uuid,balanceChangeDTO.getAmount());
//
//        when(accountService.increaseBalance(uuid, balanceChangeDTO.getAmount())).thenReturn(legalAccountDTO);
//
//        ResponseEntity<LegalAccountDto> response = balanceController.increaseAccountBalance(uuid, balanceChangeDTO);
//
//        verify(accountService, times(1)).increaseBalance(uuid, balanceChangeDTO.getAmount());
//
//        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
//        assertEquals(legalAccountDTO, response.getBody());
//    }
//
//    @Test
//    void decreaseAccountBalance() {
//        String uuid = UUID.randomUUID().toString();
//        BalanceChangeDto balanceChangeDTO = new BalanceChangeDto();
//        balanceChangeDTO.setAmount(100D);
//
//        LegalAccountDto legalAccountDTO = new LegalAccountDto(uuid,balanceChangeDTO.getAmount());
//
//        when(accountService.decreaseBalance(uuid, balanceChangeDTO.getAmount())).thenReturn(legalAccountDTO);
//
//        ResponseEntity<LegalAccountDto> response = balanceController.decreaseAccountBalance(uuid, balanceChangeDTO);
//
//        verify(accountService, times(1)).decreaseBalance(uuid, balanceChangeDTO.getAmount());
//
//        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
//        assertEquals(legalAccountDTO, response.getBody());
//
//    }
//
//    @Test
//    void grantAccessToAccount() {
//    }
//
//    @Test
//    void revokeAccessFromAccount() {
//    }
//}