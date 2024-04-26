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
    private long amount;

    /**
     * Сеттер для автоматического создания BalanceChangeDto в контроллерах.
     * Автоматически выбрасывает ошибку, если будет передано отрицательное число.
     *
     * @param amount сумма изменения дробным числом в рублях.
     *               Цифры после второго знака после запятой будут отброшены.
     */
    public void setAmount(Double amount) {
        if (amount == null)
            throw new IllegalArgumentException("Amount cannot be null");
        long rubles = (long) (double) amount;
        long kopecks = Math.round((amount - rubles) * 1000) / 10;
        this.amount = rubles * 100 + kopecks;
    }

}
