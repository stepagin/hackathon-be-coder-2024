package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "access")
@Getter
@Setter
@RequiredArgsConstructor
public class AccessEntity implements Serializable {
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

    public AccessEntity(@NonNull PersonEntity person){
        this.person = person;
    }
}
