package ru.stepagin.becoder.DTO;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDTO {
    @Nonnull
    private String id;
    @Nonnull
    private Long balance;

    public LegalAccountDTO(@Nonnull LegalAccountEntity entity) {
        this.id = String.valueOf(entity.getId());
        this.balance = entity.getBalance();
    }

    public LegalAccountDTO(@Nonnull AccessEntity accessEntity) {
        this.id = String.valueOf(accessEntity.getAccount().getId());
        this.balance = accessEntity.getAccount().getBalance();
    }
}
