package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonDto {
    private Long id;
    @NotNull(message = "не может быть null")
    private String login;
}
