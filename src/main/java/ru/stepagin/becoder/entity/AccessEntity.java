package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    @ManyToOne(optional = false)
    private PersonEntity person;
    @ManyToOne(optional = false)
    private LegalAccountEntity account;

    public AccessEntity(PersonEntity person, LegalAccountEntity account) {
        this.person = person;
        this.account = account;
    }
}
