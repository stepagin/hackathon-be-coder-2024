package ru.stepagin.becoder.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChangeUIDTO {
    @NotNull(message = "не может быть null")
    @Positive(message = "должен быть больше нуля")
    public Double amount;
}
