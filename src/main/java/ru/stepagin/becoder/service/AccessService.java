package ru.stepagin.becoder.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.repository.AccessRepository;

@Service
@AllArgsConstructor
public class AccessService {
    @Autowired
    private AccessRepository accessRepository;

    public boolean checkHasAccess(Long personId, Long accoutId) {
        AccessEntity access = null;
        try {
            access = accessRepository.findByAccount_IdAndPersonId(accoutId, personId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
        return access != null;
    }
}
