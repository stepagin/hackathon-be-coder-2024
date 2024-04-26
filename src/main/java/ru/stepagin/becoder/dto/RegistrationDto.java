package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    @NotNull(message = "не может быть null")
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String login;
    @NotNull(message = "не может быть null")
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String password;
}
