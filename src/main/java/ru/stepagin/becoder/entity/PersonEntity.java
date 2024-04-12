package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@RequiredArgsConstructor
public class PersonEntity implements Serializable {
    private Long id;
    private String login;
    private String password;


    public PersonEntity(Long id){
        this.id = id;
    }

    public PersonEntity(String username){
        this.login = username;
    }
}
