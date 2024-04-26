package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDto {
    @NotNull(message = "не может быть null")
    private String id;
    private double balance;

    public long getBalanceInRubles(double amount) {
        return (long) (amount / 100);
    }
}
