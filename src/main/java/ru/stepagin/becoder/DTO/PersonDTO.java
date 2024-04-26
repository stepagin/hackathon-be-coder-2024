package ru.stepagin.becoder.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.PersonEntity;

@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {
    private Long id;
    @NotNull(message = "не может быть null")
    private String login;

    public PersonDTO(PersonEntity entity) {
        this.setId(entity.getId());
        this.setLogin(entity.getLogin());
    }
}
