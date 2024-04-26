package ru.stepagin.becoder.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.dto.BalanceChangeDto;
import ru.stepagin.becoder.dto.BalanceChangeUiDto;
import ru.stepagin.becoder.dto.LegalAccountDto;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.PersonService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@Controller
@CrossOrigin
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class UiAccountsController {

    private final BalanceController balanceController;
    private final PersonService personService;
    private final SecurityService securityService;
    private final AccessService accessService;
    private final LegalAccountService legalAccountService;

    @GetMapping
    public String getAll(Authentication auth, Model model) {
        ResponseEntity<List<LegalAccountDto>> accounts = balanceController.getAllAccounts(auth);
        PersonEntity person = securityService.getPerson(auth);
        model.addAttribute("user", person);
        model.addAttribute("accounts", accounts.getBody());
        return "accounts";
    }

    @GetMapping("/{id}")
    public String getAccountDetails(@PathVariable String id, Model model, Authentication auth, HttpServletRequest request) {

        ResponseEntity<?> responseEntity = balanceController.getAccountDetails(id);
        String referer = request.getHeader("Referer");

        if (responseEntity.getStatusCode().equals(OK)) {
            LegalAccountDto legalAccountDTO = (LegalAccountDto) responseEntity.getBody();
            List<PersonDto> personList = personService.getAllUsers();
            List<PersonDto> usersWithAccess = personList.stream().filter(user -> accessService.checkHasAccess(user.getId(), UUID.fromString(legalAccountDTO.getId()))).toList();
            PersonEntity person = securityService.getPerson(auth);

            model.addAttribute("account", legalAccountDTO);
            model.addAttribute("balanceChangeDTO", new BalanceChangeUiDto());
            model.addAttribute("users", personList);
            model.addAttribute("usersWithAccess", usersWithAccess);
            model.addAttribute("accessUser", new PersonDto());
            model.addAttribute("isOwner", legalAccountService.isActiveOwner(person, UUID.fromString(legalAccountDTO.getId())));
            return "account_page";
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            model.addAttribute("revertAddress", referer);
            return "error";
        }
    }


    @PostMapping
    public String createAccount(Authentication authentication, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.createAccount(authentication);
        String referer = request.getHeader("Referer");

        if (responseEntity.getStatusCode().equals(OK)) {
            return "redirect:" + referer;
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            model.addAttribute("revertAddress", referer);
            return "error";
        }
    }

    @PostMapping("/{accountId}/deposit")
    public String increaseAccountBalance(@PathVariable(name = "accountId") String id, @ModelAttribute BalanceChangeUiDto balanceChange, HttpServletRequest request, Model model) {
        BalanceChangeDto dto = new BalanceChangeDto();
        dto.setAmount(balanceChange.getAmount());
        ResponseEntity<?> responseEntity = balanceController.increaseAccountBalance(id, dto);
        String referer = request.getHeader("Referer");
        if (responseEntity.getStatusCode().equals(OK)) {
            return "redirect:" + referer;
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            model.addAttribute("revertAddress", referer);
            return "error";
        }


    }

    @PostMapping("/{accountId}/withdrawal")
    public String decreaseAccountBalance(@PathVariable(name = "accountId") String id, @ModelAttribute BalanceChangeUiDto balanceChange, HttpServletRequest request, Model model) {
        BalanceChangeDto dto = new BalanceChangeDto();
        dto.setAmount(balanceChange.getAmount());
        ResponseEntity<?> responseEntity = balanceController.decreaseAccountBalance(id, dto);
        String referer = request.getHeader("Referer");
        if (responseEntity.getStatusCode().equals(OK)) {
            return "redirect:" + referer;
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            model.addAttribute("revertAddress", referer);
            return "error";
        }

    }

    @PostMapping("/{accountId}/grantment")
    public String grantAccessToAccount(@PathVariable(name = "accountId") String id, @ModelAttribute PersonDto person, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.grantAccessToAccount(id, person);
        String referer = request.getHeader("Referer");
        if (responseEntity.getStatusCode().equals(OK)) {
            return "redirect:" + referer;
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            model.addAttribute("revertAdress", referer); // TODO: fix typo
            return "error";
        }
    }

    @PostMapping("/{accountId}/revocation")
    public String revokeAccessFromAccount(@PathVariable(name = "accountId") String id, @ModelAttribute PersonDto person, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.revokeAccessFromAccount(id, person.getLogin());
        if (responseEntity.getStatusCode().equals(OK)) {
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        } else {
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }
    }

}
