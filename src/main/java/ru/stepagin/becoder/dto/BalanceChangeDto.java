package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChangeDto {
    @NotNull(message = "не может быть null")
    @Positive(message = "должен быть больше нуля")
    private double amount;

    public long getAmountInKopecks() {
        long rubles = (long) amount;
        long kopecks = Math.round((amount - rubles) * 1000) / 10;
        return rubles * 100 + kopecks;
    }

}
