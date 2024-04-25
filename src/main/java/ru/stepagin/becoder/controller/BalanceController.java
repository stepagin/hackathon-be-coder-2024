package ru.stepagin.becoder.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
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
    public String getAccountDetails(@PathVariable String id, Model model) {
        model.addAttribute("account", accountService.getAccountById(id));
        return "account_page";
    }

    @PostMapping
    public String createAccount(Authentication authentication, HttpServletRequest request) {
        PersonEntity person = securityService.getPerson(authentication);
        if (person == null) {
            throw new InvalidIdSuppliedException("Не найден пользователь, создающий счёт");
        }
        accountService.createAccount(person);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/increase")
    public ResponseEntity<LegalAccountDTO> increaseAccountBalance(@RequestBody BalanceChangeDTO balanceChange) {
        return ResponseEntity.ok(accountService.increaseBalance(balanceChange));
    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/decrease")
    public ResponseEntity<LegalAccountDTO> decreaseAccountBalance(@RequestBody BalanceChangeDTO balanceChange) {
        return ResponseEntity.ok(accountService.decreaseBalance(balanceChange));
    }


    @GetMapping("/all")
    public String getAllAccounts(Authentication auth, Model model) {
        PersonEntity person = securityService.getPerson(auth);
        model.addAttribute("accounts", accessService.getAllByPerson(person));
        return "accounts";
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
