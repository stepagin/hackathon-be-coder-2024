package ru.stepagin.becoder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.PersonService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final PersonService personService;

    public AuthController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/register")
    public ResponseEntity<PersonDTO> register(@RequestBody PersonEntity person) {
        return ResponseEntity.ok(personService.registerPerson(person.getLogin(), person.getPassword()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAll() {
        return ResponseEntity.ok(personService.getAllUsers());
    }
}
