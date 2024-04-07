package ru.stepagin.becoder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "legal_account")
@Getter
@Setter
@RequiredArgsConstructor
public class LegalAccountEntity {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "balance", nullable = false)
    private Long balance;
}
