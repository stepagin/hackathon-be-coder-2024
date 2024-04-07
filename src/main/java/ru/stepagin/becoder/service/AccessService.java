package ru.stepagin.becoder.service;

import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.repository.AccessRepository;

@Service
public class AccessService {
    private final AccessRepository accessRepository;

    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }

    public boolean checkHasAccess(Long personId, Long accoutId) {
        AccessEntity access;
        try {
            access = accessRepository.findByAccount_IdAndPersonId(accoutId, personId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
        return access != null;
    }
}
