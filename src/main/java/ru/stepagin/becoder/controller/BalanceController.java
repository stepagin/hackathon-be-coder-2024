package ru.stepagin.becoder.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.exception.InvalidIdSuppliedException;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("${api.endpoints.base-url}/accounts")
@RequiredArgsConstructor
@Tag(name = "API управления балансом")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Ошибка не стороне пользователя",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Ошибка не стороне сервера",
                content = @Content(schema = @Schema(implementation = String.class)))
})
public class BalanceController {
    private final LegalAccountService accountService;
    private final AccessService accessService;
    private final SecurityService securityService;

    @GetMapping()
    public ResponseEntity<List<LegalAccountDTO>> getAllAccounts(Authentication auth) {
        PersonEntity person = securityService.getPerson(auth);
        return ResponseEntity.ok(accessService.getAllByPerson(person));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    public ResponseEntity<LegalAccountDTO> getAccountDetails(@PathVariable String id) {
        LegalAccountDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping
    public ResponseEntity<LegalAccountDTO> createAccount(Authentication authentication) {
        PersonEntity person = securityService.getPerson(authentication);
        if (person == null) {
            throw new InvalidIdSuppliedException("Не найден пользователь, создающий счёт");
        }
        LegalAccountDTO account = accountService.createAccount(person);
        return ResponseEntity.ok(account);
    }

    // TODO: remove verbs
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<LegalAccountDTO> increaseAccountBalance(@PathVariable(name = "accountId") String id, @RequestBody BalanceChangeDTO balanceChange) {
        return ResponseEntity.ok(accountService.increaseBalance(balanceChange));
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/{accountId}/withdrawal")
    public ResponseEntity<LegalAccountDTO> decreaseAccountBalance(@PathVariable(name = "accountId") String id, @RequestBody BalanceChangeDTO balanceChange) {
        return ResponseEntity.ok(accountService.decreaseBalance(balanceChange));
    }

    // TODO put
    @PutMapping("/{accountId}/permissions")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> grantAccessToAccount(@PathVariable(name = "accountId") String id, @RequestBody PersonDTO person) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        accessService.grantAccess(account, person.getLogin());
        return ResponseEntity.ok("Пользователю " + person.getLogin() + " выдан доступ к аккаунту " + id);
    }

    @PutMapping("/{accountId}/permissions/{permission_id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> revokeAccessFromAccount(@PathVariable(name = "accountId") String id, @RequestBody PersonDTO person) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        accessService.revokeAccess(account, person.getLogin());
        return ResponseEntity.ok("У пользователя " + person.getLogin() + " отозван доступ к аккаунту " + id);
    }
}
