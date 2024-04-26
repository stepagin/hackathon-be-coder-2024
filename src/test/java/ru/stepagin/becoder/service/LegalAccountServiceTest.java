package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.LegalAccountRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LegalAccountServiceTest {

    @Mock
    private LegalAccountRepository legalAccountRepository;
    @Mock
    private HistoryService historyService;
    @Mock
    private AccessRepository accessRepository;
    private LegalAccountService legalAccountService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        legalAccountService = new LegalAccountService(legalAccountRepository, historyService, accessRepository);
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
        verify(accessRepository, times(1)).save(any(AccessEntity.class));
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

        LegalAccountEntity account = new LegalAccountEntity(person);
        AccessEntity access = new AccessEntity(person, account);

        when(legalAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(account));
        when(accessRepository.findByAccountIdAndPersonId(uuid, person.getId())).thenReturn(access);

        legalAccountService.isActiveOwner(person, uuid);

        verify(legalAccountRepository, times(1)).findById(any(UUID.class));
        verify(accessRepository, times(1)).findByAccountIdAndPersonId(uuid, person.getId());

    }


    @Test
    void decreaseBalance() {
        LegalAccountEntity legalAccount = new LegalAccountEntity();
        legalAccount.setBalance(12300L);
        UUID uuid = UUID.randomUUID();
        legalAccount.setId(uuid);

        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setAmount(123D);

        legalAccountService.decreaseBalance(legalAccount, balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).decreaseBalanceById(any(UUID.class), any(Long.class));
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

        legalAccountService.increaseBalance(legalAccount, balanceChange.getAmount());

        verify(legalAccountRepository, times(1)).increaseBalanceById(any(UUID.class), any(Long.class));
        verify(historyService, times(1)).addRecord(any(Long.class), any(LegalAccountEntity.class), any());


    }

}