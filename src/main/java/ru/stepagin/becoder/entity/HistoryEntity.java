package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
public class HistoryEntity implements Serializable {

    private Long id;
    private Long amount;
    private LegalAccountEntity account;
    private LocalDateTime date;
    private boolean success;


    public HistoryEntity(Long amount, LegalAccountEntity legalAccountEntity, Boolean success){
        this.amount = amount;
        this.account = legalAccountEntity;
        this.date = LocalDateTime.now();
        this.success = success;
    }
}
