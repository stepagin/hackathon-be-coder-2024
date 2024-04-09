package ru.stepagin.becoder.controller;

import lombok.NonNull;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.service.LegalAccountService;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class BalanceCotroller {
    private final LegalAccountService accountService;

    public BalanceCotroller(LegalAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    //@PreAuthorize("@securityService.isAuthorized(#id, authentication)")
    public ResponseEntity<?> getAccountDetails(@PathVariable Long id) {
        try {
            LegalAccountDTO account = accountService.getAccountById(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    //@PreAuthorize("@securityService.isAuthorized(#id, authentication)")
    public ResponseEntity<?> createAccount(@PathVariable Long id) {
        try {
            LegalAccountDTO account = accountService.createAccount(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


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
}
