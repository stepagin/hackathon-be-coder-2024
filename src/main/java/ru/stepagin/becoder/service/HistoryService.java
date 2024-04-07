package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.repository.HistoryRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void addRecord(@Nonnull Long amount, @Nonnull LegalAccountEntity legalAccountEntity, @Nonnull Boolean success) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setAccount(legalAccountEntity);
        historyEntity.setAmount(amount);
        historyEntity.setSuccess(success);
        historyEntity.setDate(LocalDateTime.now());
        try {
            historyRepository.save(historyEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Произошла ошибка во время сохранения в историю запросов");
        }
    }
}
