package ru.stepagin.becoder.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationDTO {
    @NotNull
    @Size(min = 6, max = 255)
    private String login;
    @NotNull
    @Size(min = 6, max = 255)
    private String password;
}
