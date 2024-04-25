package ru.stepagin.becoder.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.PersonService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("${api.endpoints.base-url}/auth")
@RequiredArgsConstructor
@Tag(name = "API авторизации")
@ApiResponse(responseCode = "500", description = "Ошибка не стороне сервера",
        content = @Content(schema = @Schema(implementation = String.class)))
public class AuthController {
    private final PersonService personService;

    @PostMapping("/register")
    public ResponseEntity<PersonDTO> register(@RequestBody PersonEntity person) {
        return ResponseEntity.ok(personService.registerPerson(person.getLogin(), person.getPassword()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAll() {
        return ResponseEntity.ok(personService.getAllUsers());
    }
}
