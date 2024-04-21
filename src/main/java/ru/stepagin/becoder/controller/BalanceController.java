package ru.stepagin.becoder.controller;

import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.AccessDTO;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class BalanceController {
    private final LegalAccountService accountService;
    private final AccessService accessService;
    private final SecurityService securityService;

    public BalanceController(LegalAccountService accountService, AccessService accessService, SecurityService securityService) {
        this.accountService = accountService;
        this.accessService = accessService;
        this.securityService = securityService;
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
        if (person == null) ResponseEntity.badRequest().body("пользователь, создающий аккаунт, не найден");
        LegalAccountDTO account = accountService.createAccount(person);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/increase")
    public ResponseEntity<String> increaseAccountBalance(@RequestBody @NonNull BalanceChangeDTO balanceChange) {
        // check increasing amount > 0
        if (balanceChange.getAmount() <= 0L)
            throw new IllegalArgumentException("Amount должно быть больше нуля");

        accountService.increaseBalance(balanceChange);
        return ResponseEntity.ok("Счёт успешно пополнен");
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/decrease")
    public ResponseEntity<String> decreaseAccountBalance(@RequestBody @NonNull BalanceChangeDTO balanceChange) {
        accountService.decreaseBalance(balanceChange);
        return ResponseEntity.ok("Оплата прошла успешно");
    }


    @GetMapping("/all")
    public ResponseEntity<List<AccessDTO>> getAllAccounts(Authentication auth) {
        PersonEntity person = securityService.getPerson(auth);
        return ResponseEntity.ok(accessService.getAllByPerson(person));
    }

    @PostMapping("grant/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> grantAccessToAccount(@PathVariable String id, @RequestBody PersonDTO person) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        if (accessService.grantAccess(account, person.getLogin()))
            return ResponseEntity.ok("Пользователю " + person.getLogin() + " выдан доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на выдачу прав");
    }

    @PostMapping("revoke/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> revokeAccessFromAccount(@PathVariable String id, @RequestBody PersonDTO person) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        if (accessService.revokeAccess(account, person.getLogin()))
            return ResponseEntity.ok("У пользователя " + person.getLogin() + " отозван доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на отзыв прав");
    }
}
