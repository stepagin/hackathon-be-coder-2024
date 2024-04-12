package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
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
    @ManyToOne
    private PersonEntity person;
    @ManyToOne
    private LegalAccountEntity account;

    public AccessEntity(@NonNull PersonEntity person, @NonNull LegalAccountEntity account){
        this.person = person;
        this.account = account;
    }
}
