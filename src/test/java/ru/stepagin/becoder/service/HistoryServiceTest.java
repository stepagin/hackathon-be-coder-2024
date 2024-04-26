package ru.stepagin.becoder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.HistoryRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {
    @Mock
    private HistoryRepository historyRepository;

    private HistoryService historyService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        historyService = new HistoryService(historyRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addRecord() {
        Long amount = 100L;
        LegalAccountEntity legalAccountEntity = new LegalAccountEntity();
        Boolean success = true;

        HistoryEntity history = new HistoryEntity(amount, legalAccountEntity, success);

        historyService.addRecord(amount, legalAccountEntity, success);

        ArgumentCaptor<HistoryEntity> historyEntityArgumentCaptor = ArgumentCaptor.forClass(HistoryEntity.class);
        verify(historyRepository).save(historyEntityArgumentCaptor.capture());

        HistoryEntity result = historyEntityArgumentCaptor.getValue();

        assertEquals(history.getAccount(), result.getAccount());
    }
}