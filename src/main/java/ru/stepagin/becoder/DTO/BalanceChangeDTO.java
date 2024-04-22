package ru.stepagin.becoder.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChangeDTO {
    private Long amount;
    private LegalAccountDTO account;

    /**
     * Сеттер для автоматического создания BalanceChangeDTO в контроллерах.
     * Автоматически выбрасывает ошибку, если будет передано отрицательное число.
     *
     * @param amount сумма изменения дробным числом в рублях.
     *               Цифры после второго знака после запятой будут отброшены.
     */
    public void setAmount(double amount) {
        long rubles = (long) amount;
        long kopecks = Math.round((amount - rubles) * 1000) / 10 ;
        checkAmount(rubles * 100 + kopecks);
        this.amount = rubles * 100 + kopecks;
    }

    private void checkAmount(long kopecks) throws IllegalArgumentException {
        // check increasing amount > 0
        if (kopecks <= 0L)
            throw new IllegalArgumentException("amount должно быть больше нуля");
    }

    public boolean checkDontHaveNulls() {
        return (getAccount() != null && getAmount() != null && getAccount().getId() != null);
    }
}
