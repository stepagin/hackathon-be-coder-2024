package ru.stepagin.becoder.entity;

import lombok.Getter;
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

    public AccessEntity(Long personId, UUID accountId){
        this.person = new PersonEntity(personId);
        this.account = new LegalAccountEntity(accountId);
    }
}
