package ru.stepagin.becoder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.DTO.RegistrationDTO;
import ru.stepagin.becoder.service.PersonService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("${api.endpoints.base-url}/auth")
@RequiredArgsConstructor
@Tag(name = "API авторизации")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно выполнено"),
        @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента",
                content = @Content(schema = @Schema(implementation = String.class)))
})
public class AuthController {
    private final PersonService personService;

    @Operation(summary = "Зарегистрироваться в системе")
    @PostMapping("/register")
    public ResponseEntity<PersonDTO> register(@RequestBody @Validated RegistrationDTO data) {
        return ResponseEntity.ok(personService.registerPerson(data.getLogin(), data.getPassword()));
    }

    @Operation(summary = "Получить список пользователей, зарегистрированных в системе")
    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAll() {
        return ResponseEntity.ok(personService.getAllUsers());
    }
}
