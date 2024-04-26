package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChangeUiDto {
    @NotNull(message = "не может быть null")
    @Positive(message = "должен быть больше нуля")
    public Double amount;
}
