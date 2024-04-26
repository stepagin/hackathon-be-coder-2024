package ru.stepagin.becoder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDto {
    @NotNull(message = "не может быть null")
    private String id;
    private double balance;

    public LegalAccountDto(String id, double kopecks) {
        this.id = id;
        this.balance = kopecks;
    }

    public LegalAccountDto(LegalAccountEntity entity) {
        this(
                String.valueOf(entity.getId()),
                (double) entity.getBalance() / 100
        );
    }

    public LegalAccountDto(AccessEntity accessEntity) {
        this(
                String.valueOf(accessEntity.getAccount().getId()),
                accessEntity.getAccount().getBalance()
        );
    }
}
