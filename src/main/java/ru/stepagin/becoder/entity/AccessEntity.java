package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "access")
@Getter
@Setter
@RequiredArgsConstructor
public class AccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private PersonEntity person;
    @ManyToOne
    private LegalAccountEntity account;


    public AccessEntity(Long personId, UUID accountId){
        this.person = new PersonEntity(personId);
        this.account = new LegalAccountEntity(accountId);
    }
}
