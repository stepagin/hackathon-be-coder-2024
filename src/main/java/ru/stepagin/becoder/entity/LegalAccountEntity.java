package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@RequiredArgsConstructor
public class LegalAccountEntity implements Serializable {
    private UUID id;
    private Long balance;


    public LegalAccountEntity(UUID accountId){
        this.id = accountId;
    }
    public LegalAccountEntity(Long balance){
        this.balance = balance;
    }

    public LegalAccountEntity(UUID accountId, Long balance){
        this.id = accountId;
        this.balance = balance;
    }
}
