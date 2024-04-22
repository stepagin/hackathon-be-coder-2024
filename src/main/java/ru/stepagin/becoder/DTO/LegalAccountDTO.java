package ru.stepagin.becoder.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Getter
@Setter
@NoArgsConstructor
public class LegalAccountDTO {
    private String id;
    private double balance;

    public LegalAccountDTO(String id, double kopecks) {
        this.id = id;
        this.balance = kopecks / 100;
    }

    public LegalAccountDTO(LegalAccountEntity entity) {
        this (
                String.valueOf(entity.getId()),
                (double) entity.getBalance()
        );
    }

    public LegalAccountDTO(AccessEntity accessEntity) {
        this (
                String.valueOf(accessEntity.getAccount().getId()),
                accessEntity.getAccount().getBalance()
        );
    }
}
