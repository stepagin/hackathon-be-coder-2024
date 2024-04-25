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
import ru.stepagin.becoder.service.PersonService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping("/account")
public class BalanceController {
    private final LegalAccountService accountService;
    private final AccessService accessService;
    private final SecurityService securityService;
    private final PersonService personService;
    private final LegalAccountService legalAccountService;

    public BalanceController(LegalAccountService accountService, AccessService accessService, SecurityService securityService, PersonService personService, LegalAccountService legalAccountService) {
        this.accountService = accountService;
        this.accessService = accessService;
        this.securityService = securityService;
        this.personService = personService;
        this.legalAccountService = legalAccountService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    public String getAccountDetails(@PathVariable String id, Model model, Authentication auth) {
        LegalAccountDTO legalAccountDTO = accountService.getAccountById(id);
        List<PersonDTO> personList = personService.getAllUsers();
        List<PersonDTO> usersWithAccess = personList.stream().filter(user->accessService.checkHasAccess(user.getId(),  UUID.fromString(legalAccountDTO.getId()))).toList();
        PersonEntity person = securityService.getPerson(auth);
        model.addAttribute("account", legalAccountDTO);
        model.addAttribute("balanceChangeDTO", new BalanceChangeDTO(legalAccountDTO));
        model.addAttribute("users", personList);
        model.addAttribute("usersWithAccess", usersWithAccess);
        model.addAttribute("accessUser", new PersonDTO());
        model.addAttribute("isOwner", legalAccountService.isActiveOwner(person, UUID.fromString(legalAccountDTO.getId())));
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
    public String increaseAccountBalance(@ModelAttribute BalanceChangeDTO balanceChange,HttpServletRequest request) {
        accountService.increaseBalance(balanceChange);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;

    }

    @PreAuthorize("@securityService.hasAccessToAccount(#balanceChange, authentication)")
    @PostMapping("/decrease")
    public String decreaseAccountBalance(@ModelAttribute BalanceChangeDTO balanceChange, HttpServletRequest request) {
        accountService.decreaseBalance(balanceChange);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;

    }


    @GetMapping("/all")
    public String getAllAccounts(Authentication auth, Model model) {
        PersonEntity person = securityService.getPerson(auth);
        model.addAttribute("accounts", accessService.getAllByPerson(person));
        return "accounts";
    }

    @PostMapping("grant/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> grantAccessToAccount(@PathVariable String id, @ModelAttribute PersonDTO person) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);

        person = personService.findById(person.getId()); //TODO это костыль, потому что в PersonDTO приходит login=null

        if (accessService.grantAccess(account, person.getLogin()))
            return ResponseEntity.ok("Пользователю " + person.getLogin() + " выдан доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на выдачу прав");
    }

    @PostMapping("revoke/{id}")
    @PreAuthorize("@securityService.isActiveOwner(#id, authentication)")
    public ResponseEntity<String> revokeAccessFromAccount(@PathVariable String id, @ModelAttribute PersonDTO person) {

        person = personService.findById(person.getId()); //TODO это костыль, потому что в PersonDTO приходит login=null

        LegalAccountEntity account = accountService.getAccountEntityById(id);
        if (accessService.revokeAccess(account, person.getLogin()))
            return ResponseEntity.ok("У пользователя " + person.getLogin() + " отозван доступ к аккаунту " + id);
        else return ResponseEntity.badRequest().body("некорректный запрос на отзыв прав");
    }
}
