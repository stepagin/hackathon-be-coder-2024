package ru.stepagin.becoder.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.BalanceChangeDTO;
import ru.stepagin.becoder.DTO.BalanceChangeUIDTO;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.DTO.PersonDTO;
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
public class UIController {

    private final BalanceController balanceController;
    private final PersonService personService;
    private final SecurityService securityService;
    private final AccessService accessService;
    private final LegalAccountService legalAccountService;

    @GetMapping
    public String getAll(Authentication auth, Model model){
        ResponseEntity<List<LegalAccountDTO>> accounts = balanceController.getAllAccounts(auth);
        model.addAttribute("accounts", accounts.getBody());
        return "accounts";
    }

    @GetMapping("/{id}")
    public String getAccountDetails(@PathVariable String id, Model model, Authentication auth) {

        ResponseEntity<?> responseEntity = balanceController.getAccountDetails(id);
        if(responseEntity.getStatusCode().equals(OK)){
            LegalAccountDTO legalAccountDTO = (LegalAccountDTO) responseEntity.getBody();
            List<PersonDTO> personList = personService.getAllUsers();
            List<PersonDTO> usersWithAccess = personList.stream().filter(user->accessService.checkHasAccess(user.getId(),  UUID.fromString(legalAccountDTO.getId()))).toList();
            PersonEntity person = securityService.getPerson(auth);

            model.addAttribute("account", legalAccountDTO);
            model.addAttribute("balanceChangeDTO", new BalanceChangeUIDTO());
            model.addAttribute("users", personList);
            model.addAttribute("usersWithAccess", usersWithAccess);
            model.addAttribute("accessUser", new PersonDTO());
            model.addAttribute("isOwner", legalAccountService.isActiveOwner(person, UUID.fromString(legalAccountDTO.getId())));
            return "account_page";
        }
        else {
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }
    }


    @PostMapping
    public String createAccount(Authentication authentication, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.createAccount(authentication);
        if(responseEntity.getStatusCode().equals(OK)){
            String referer = request.getHeader("Referer");
            return "redirect:"+ referer;
        }
        else{
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }
    }

    @PostMapping("/{accountId}/deposit")
    public String increaseAccountBalance(@PathVariable(name = "accountId") String id, @ModelAttribute BalanceChangeUIDTO balanceChange, HttpServletRequest request, Model model) {
        BalanceChangeDTO dto = new BalanceChangeDTO();
        dto.setAmount(balanceChange.getAmount());
        ResponseEntity<?> responseEntity = balanceController.increaseAccountBalance(id, dto);
        if(responseEntity.getStatusCode().equals(OK)) {
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        else{
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }


    }

    @PostMapping("/{accountId}/withdrawal")
    public String decreaseAccountBalance(@PathVariable(name = "accountId") String id, @ModelAttribute BalanceChangeUIDTO balanceChange, HttpServletRequest request, Model model) {
        BalanceChangeDTO dto = new BalanceChangeDTO();
        dto.setAmount(balanceChange.getAmount());
        ResponseEntity<?> responseEntity = balanceController.decreaseAccountBalance(id, dto);
        if(responseEntity.getStatusCode().equals(OK)){
            String referer = request.getHeader("Referer");
            return "redirect:"+ referer;
        }
        else{
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }

    }

    @PostMapping("/{accountId}/grantment")
    public String grantAccessToAccount(@PathVariable(name = "accountId") String id, @ModelAttribute PersonDTO person, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.grantAccessToAccount(id, person);
        if(responseEntity.getStatusCode().equals(OK)){
            String referer = request.getHeader("Referer");
            return "redirect:"+ referer;
        }
        else{
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }
    }

    @PostMapping("/{accountId}/revocation")
    public String revokeAccessFromAccount(@PathVariable(name = "accountId") String id, @ModelAttribute PersonDTO person, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = balanceController.revokeAccessFromAccount(id, person);
        if(responseEntity.getStatusCode().equals(OK)){
            String referer = request.getHeader("Referer");
            return "redirect:"+ referer;
        }
        else{
            model.addAttribute("errorText", responseEntity.getBody());
            return "error";
        }
    }

}