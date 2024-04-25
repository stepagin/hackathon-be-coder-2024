package ru.stepagin.becoder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.HistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void addRecord(Long amount, LegalAccountEntity legalAccountEntity, Boolean success) {
        HistoryEntity historyEntity = new HistoryEntity(amount, legalAccountEntity, success);
        historyRepository.save(historyEntity);
    }
}
