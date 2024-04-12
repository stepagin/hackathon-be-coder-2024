package ru.stepagin.becoder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.repository.AccessRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccessService {
    private final AccessRepository accessRepository;

    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }

    public void save(AccessEntity access){
        accessRepository.save(access);
    }

    public boolean checkHasAccess(Long personId, UUID accoutId) {
        AccessEntity access;
        try {
            access = accessRepository.findByAccount_IdAndPersonId(accoutId, personId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка в процессе проверки доступа к счёту");
        }
        return access != null;
    }

    public List<AccessDTO> getAllByUserId(Long id){
        return accessRepository.findAll()
                .stream()
                .filter(x -> x.getPerson().getId().equals(id))
                .map(x -> new AccessDTO(x.getId(), x.getAccount().getId().toString()))
                .collect(Collectors.toList());
    }
}
