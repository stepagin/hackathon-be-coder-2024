package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "legal_account")
@Getter
@Setter
@RequiredArgsConstructor
public class LegalAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    @Column(name = "balance", nullable = false)
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
