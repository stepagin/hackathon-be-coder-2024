package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter
@Setter
@RequiredArgsConstructor
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "amount", nullable = false)
    private Long amount;
    @ManyToOne(optional = false)
    private LegalAccountEntity account;
    @CreatedDate
    @Column(name = "date", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime date = LocalDateTime.now();
    @Column(name = "success", nullable = false)
    private boolean success;

    public HistoryEntity(Long amount, LegalAccountEntity account, boolean success) {
        this.amount = amount;
        this.account = account;
        this.success = success;
    }
}
