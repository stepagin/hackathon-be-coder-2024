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

    @NonNull
    private Long id;

    public AccessDTO(@NonNull Long id, @NonNull String accountId){
        this.id = id;
        this.accountId = accountId;
    }
}
