package ru.stepagin.becoder.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDTO {
    @NotNull(message = "не может быть null")
    private String id;
    private double balance;

    public LegalAccountDTO(String id, double kopecks) {
        this.id = id;
        this.balance = kopecks;
    }

    public LegalAccountDTO(LegalAccountEntity entity) {
        this (
                String.valueOf(entity.getId()),
                (double) entity.getBalance() / 100
        );
    }

    public LegalAccountDTO(AccessEntity accessEntity) {
        this (
                String.valueOf(accessEntity.getAccount().getId()),
                accessEntity.getAccount().getBalance()
        );
    }
}
