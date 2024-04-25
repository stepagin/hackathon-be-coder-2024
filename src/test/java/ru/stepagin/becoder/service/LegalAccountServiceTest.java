package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LegalAccountServiceTest {

    @Mock
    private LegalAccountRepository legalAccountRepository;
    @Mock
    private HistoryService historyService;
    @Mock
    private AccessService accessService;
    @InjectMocks
    private LegalAccountService legalAccountService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        legalAccountService = new LegalAccountService(legalAccountRepository, historyService, accessService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createAccount() {
        PersonEntity person = new PersonEntity("user", "user");
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity(person);

        when(legalAccountRepository.save(any(LegalAccountEntity.class))).thenReturn(legalAccountEntity);

        legalAccountService.createAccount(person);
        verify(accessService, times(1)).save(any(AccessEntity.class));
        verify(legalAccountRepository, times(1)).save(any(LegalAccountEntity.class));

    }

    @Test
    void getAccountEntityById() {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity();
        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccountEntity));

        String uuid = UUID.randomUUID().toString();
        legalAccountService.getAccountEntityById(uuid);
        verify(legalAccountRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    void getAccountById() {
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity();
        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccountEntity));

        String uuid = UUID.randomUUID().toString();
        legalAccountService.getAccountById(uuid);
        verify(legalAccountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void isActiveOwner() {
        PersonEntity person = new PersonEntity("user", "user");
        person.setId(1L);
        UUID uuid = UUID.randomUUID();

        when(legalAccountRepository.findById(uuid)).thenReturn(Optional.of(new LegalAccountEntity(person)));
        when(accessService.checkHasAccess(any(Long.class), any(UUID.class))).thenReturn(true);


        legalAccountService.isActiveOwner(person, uuid);

        verify(legalAccountRepository, times(1)).findById(any(UUID.class));
        verify(accessService, times(1)).checkHasAccess(any(Long.class), any(UUID.class));
    }


    @Test
    void updateBalance() {
        UUID uuid = UUID.randomUUID();
        Long balance = 1L;
        legalAccountRepository.updateBalanceByAccountId(uuid, balance);
        verify(legalAccountRepository, times(1)).updateBalanceByAccountId(any(UUID.class), any(Long.class));
    }

    @Test
    void decreaseBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(12300L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(123D);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccount));

        LegalAccountDTO accountDTO = legalAccountService.decreaseBalance(uuid.toString(), balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).updateBalanceByAccountId(any(UUID.class), any(Long.class));
        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());


        Assertions.assertEquals(0, accountDTO.getBalance());
    }

    @Test
    void decreaseNullBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(10000L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();

        assertThrows(InvalidIdSuppliedException.class, () -> legalAccountService.decreaseBalance(uuid.toString(), balanceChange.getAmount()));

    }

    @Test
    void decreaseBalanceWhenNotEnough() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(10000L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);


        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(200D);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccount));

        assertThrows(IllegalArgumentException.class, () -> legalAccountService.decreaseBalance(uuid.toString(), balanceChange.getAmount()));

        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());

    }

    @Test
    void increaseBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(0L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(123D);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccount));

        LegalAccountDTO accountDTO = legalAccountService.increaseBalance(uuid.toString(), balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).updateBalanceByAccountId(any(UUID.class), any(Long.class));
        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());


        Assertions.assertEquals(123, accountDTO.getBalance());
    }

    @Test
    void increaseNullBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(10000L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();


        assertThrows(InvalidIdSuppliedException.class, () -> legalAccountService.increaseBalance(uuid.toString(), balanceChange.getAmount()));

    }

    @Test
    void increaseRealBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(10L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(0.3);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccount));

        LegalAccountDTO accountDTO = legalAccountService.increaseBalance(uuid.toString(), balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).updateBalanceByAccountId(any(UUID.class), any(Long.class));
        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());


        Assertions.assertEquals(0.4, accountDTO.getBalance());
    }

    @Test
    void decreaseRealBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(100L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(0.5);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(legalAccount));

        LegalAccountDTO accountDTO = legalAccountService.decreaseBalance(uuid.toString(), balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).updateBalanceByAccountId(any(UUID.class), any(Long.class));
        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());


        Assertions.assertEquals(0.5, accountDTO.getBalance());
    }
}