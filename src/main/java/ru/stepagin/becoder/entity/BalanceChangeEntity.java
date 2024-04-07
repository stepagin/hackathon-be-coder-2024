package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance_change")
@Getter
@Setter
@RequiredArgsConstructor
public class BalanceChangeEntity {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "amount")
    private Long amount;
    @ManyToOne
    private PersonEntity account;
    @CreatedDate
    @Column(name = "date")
    private LocalDateTime date;
}
