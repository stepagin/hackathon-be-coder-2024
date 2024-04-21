package ru.stepagin.becoder.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ru.stepagin.becoder.entity.AccessEntity;

@Getter
@Setter
@NoArgsConstructor
public class AccessDTO {
    @NonNull
    private String accountId;

    public AccessDTO(@NonNull String accountId) {
        this.accountId = accountId;
    }

    public AccessDTO(@NonNull AccessEntity accessEntity) {
        this.accountId = String.valueOf(accessEntity.getAccount().getId());
    }
}
