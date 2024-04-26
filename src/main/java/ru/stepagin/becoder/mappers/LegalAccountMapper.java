package ru.stepagin.becoder.mappers;

import ru.stepagin.becoder.dto.LegalAccountDto;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;

import java.util.List;

public abstract class LegalAccountMapper {

    public static LegalAccountDto toDto(LegalAccountEntity account) {
        LegalAccountDto legalAccountDto = new LegalAccountDto();
        legalAccountDto.setId(String.valueOf(account.getId()));
        legalAccountDto.setBalance(Double.valueOf(account.getBalance()) / 100);
        return legalAccountDto;
    }

    public static LegalAccountDto toDto(AccessEntity accessEntity) {
        return LegalAccountMapper.toDto(accessEntity.getAccount());
    }

    public static List<LegalAccountDto> toDtos(List<LegalAccountEntity> entities) {
        return entities.stream().map(LegalAccountMapper::toDto).toList();
    }
}
