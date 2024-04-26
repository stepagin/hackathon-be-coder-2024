package ru.stepagin.becoder.mappers;

import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.PersonEntity;

import java.util.List;

public abstract class PersonMapper {
    public static PersonDto toDto(PersonEntity person) {
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setLogin(person.getLogin());
        return personDto;
    }

    public static PersonDto toDto(AccessEntity accessEntity) {
        return PersonMapper.toDto(accessEntity.getPerson());
    }

    public static List<PersonDto> toDtos(List<PersonEntity> persons) {
        return persons.stream().map(PersonMapper::toDto).toList();
    }
}
