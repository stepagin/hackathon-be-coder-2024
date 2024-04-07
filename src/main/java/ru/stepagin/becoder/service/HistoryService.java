package ru.stepagin.becoder.service;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.repository.HistoryRepository;

@Service
@AllArgsConstructor
public class HistoryService {
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private LegalAccountService legalAccountService;

    public void addRecord(@Nonnull Long amount, @Nonnull Long legalAccountId, @Nonnull Boolean success) {
        HistoryEntity entity = new HistoryEntity();
        legalAccountService.setLegalAccountById(entity, legalAccountId);
        entity.setAmount(amount);
        entity.setSuccess(success);
        try {
            historyRepository.save(entity);
        } catch (Exception e) {
            throw new IllegalArgumentException("Произошла ошибка во время сохранения в историю запросов");
        }
    }
}
