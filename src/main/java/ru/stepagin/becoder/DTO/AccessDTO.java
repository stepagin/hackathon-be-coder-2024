package ru.stepagin.becoder.DTO;

import lombok.*;
import ru.stepagin.becoder.entity.AccessEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessDTO {
    @NonNull
    private String accountId;

    public AccessDTO(@NonNull AccessEntity accessEntity) {
        this.accountId = String.valueOf(accessEntity.getAccount().getId());
    }
}
