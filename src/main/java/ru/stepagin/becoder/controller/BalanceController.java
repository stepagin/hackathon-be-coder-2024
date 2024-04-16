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
    public ResponseEntity<?> getAccountDetails(@PathVariable String id) {
        try {
            LegalAccountDTO account = accountService.getAccountById(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createAccount(Authentication authentication) {
        try {
            PersonEntity person = securityService.getPerson(authentication);
            if(person == null) ResponseEntity.badRequest().body("пользователь, создающий аккаунт, не найден");
            LegalAccountDTO account = accountService.createAccount(person);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/increase")
    public ResponseEntity<String> increaseAccountBalance(@RequestBody @NonNull BalanceChangeDTO balanceChange) {
        try {
            // check increasing amount > 0
            if (balanceChange.getAmount() <= 0L)
                throw new IllegalArgumentException("Amount должно быть больше нуля");

            accountService.increaseBalance(balanceChange);
            return ResponseEntity.ok("Счёт успешно пополнен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/decrease")
    public ResponseEntity<String> decreaseAccountBalance(@RequestBody @NonNull BalanceChangeDTO balanceChange) {
        try {
            // check decreasing amount > 0
            if (balanceChange.getAmount() <= 0L)
                throw new IllegalArgumentException("Amount должно быть больше нуля");

            accountService.decreaseBalance(balanceChange);
            return ResponseEntity.ok("Оплата прошла успешно");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/all/{id}")
    @PreAuthorize("@securityService.checkIdIsSame(#id, authentication)")
    public ResponseEntity<List<AccessDTO>> getAllAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(accessService.getAllByUserId(id));
    }

    @PostMapping("grant/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> grantAccessToAccount(@PathVariable String id, @RequestBody PersonDTO person){
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        if(accessService.grantAccess(account, person.getLogin())) return ResponseEntity.ok("Пользователю " + person.getLogin() + "выдан доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на выдачу прав");
    }

    @PostMapping("revoke/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> revokeAccessFromAccount(@PathVariable String id, @RequestBody PersonDTO person){
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        if(accessService.revokeAccess(account, person.getLogin())) return ResponseEntity.ok("У пользователя " + person.getLogin() + "отозван доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на отзыв прав");
    }
}
