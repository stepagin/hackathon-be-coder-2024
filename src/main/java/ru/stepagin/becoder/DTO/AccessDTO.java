package ru.stepagin.becoder.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccessDTO {
    @NonNull
    private String accountId;

    public AccessDTO(@NonNull String accountId) {
        this.accountId = accountId;
    }
}
