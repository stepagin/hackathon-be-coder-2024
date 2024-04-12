package ru.stepagin.becoder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.PersonService;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    PersonService personService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PersonEntity person) {
        try {
            return ResponseEntity.ok(personService.registerPerson(person.getLogin(), person.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(personService.getAllUsers());
    }
}
