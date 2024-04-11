package ru.stepagin.becoder.microservicesConnection;

import lombok.Data;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.HistoryEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;

import java.io.Serializable;
import java.util.UUID;


@Data
public class Message implements Serializable {
    private AccessEntity access;
    private HistoryEntity history;
    private LegalAccountEntity legalAccount;
    private PersonEntity person;


    public Message(){}

    public Message(AccessEntity access){
        this.access = access;
    }
    public Message(HistoryEntity history){
        this.history = history;
    }
    public Message(LegalAccountEntity legalAccount){
        this.legalAccount = legalAccount;
    }
    public Message(PersonEntity person){
        this.person = person;
    }


}
