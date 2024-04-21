package ru.stepagin.becoder.DTO;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChangeDTO {
    @Nonnull
    private Long amount;
    @Nonnull
    private LegalAccountDTO account;

    /**
     * Конструктор для корректной обработки копеек. Используются минорные единицы - копейки.
     *
     * @param amount  double значение изменения баланса. Конвертируется в long значение копеек.
     * @param account данные юридического счёта.
     */
    public BalanceChangeDTO(double amount, @Nonnull LegalAccountDTO account) {
        long rubles = (long) amount;
        long kopecks = (long) ((amount - rubles) * 100);
        this.amount = rubles * 100 + kopecks;
        this.account = account;
    }

    /**
     * Конструктор с обработкой целых рублей.
     *
     * @param rubles  число рублей.
     * @param account данные юридического счёта.
     */
    public BalanceChangeDTO(long rubles, @Nonnull LegalAccountDTO account) {
        this.amount = rubles * 100;
        this.account = account;
    }
}
