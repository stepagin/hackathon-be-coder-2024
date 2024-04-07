package ru.stepagin.becoder.DTO;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDTO {
    @Nonnull
    private Long id;
    @Nonnull
    private Long balance;
}
