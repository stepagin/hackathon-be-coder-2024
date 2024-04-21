package ru.stepagin.becoder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.stepagin.becoder.config.SecurityConfiguration;

@Entity
@Table(name = "person")
@Getter
@Setter
@RequiredArgsConstructor
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "password", nullable = false)
    private String password;

    public PersonEntity(String login, String passwordNotEncoded) {
        this.login = login;
        this.password = SecurityConfiguration.passwordEncoder().encode(passwordNotEncoded);
    }
}
