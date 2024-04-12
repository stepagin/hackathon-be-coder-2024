package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "person")
@Getter
@Setter
@RequiredArgsConstructor
public class PersonEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "password", nullable = false)
    private String password;

    public PersonEntity(Long id){
        this.id = id;
    }

    public PersonEntity(String username){
        this.login = username;
    }

    public PersonEntity(String username, String password){
        this.login = username;
        this.password = password;
    }
}
