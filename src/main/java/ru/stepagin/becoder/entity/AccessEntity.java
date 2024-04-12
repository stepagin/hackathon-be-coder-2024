package ru.stepagin.becoder.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@RequiredArgsConstructor
public class AccessEntity implements Serializable {

    private Long id;
    private PersonEntity person;
    private LegalAccountEntity account;


    public AccessEntity(@NonNull PersonEntity person, @NonNull LegalAccountEntity account){
        this.person = person;
        this.account = account;
    }

    public AccessEntity(@NonNull PersonEntity person){
        this.person = person;
    }
}
