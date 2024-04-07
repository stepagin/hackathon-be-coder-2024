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
    private PersonDTO person;
    @Nonnull
    private Long amount;
    @Nonnull
    private LegalAccountDTO account;
}
