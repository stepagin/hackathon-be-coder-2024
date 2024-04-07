package ru.stepagin.becoder.DTO;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDTO {
    @Nonnull
    private Long id;
    @Nonnull
    private Long balance;

    public LegalAccountDTO(@Nonnull LegalAccountEntity entity) {
        this.id = entity.getId();
        this.balance = entity.getBalance();
    }
}
