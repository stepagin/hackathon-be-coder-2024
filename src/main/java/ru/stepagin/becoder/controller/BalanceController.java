package ru.stepagin.becoder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.dto.BalanceChangeDto;
import ru.stepagin.becoder.dto.LegalAccountDto;
import ru.stepagin.becoder.dto.PersonDto;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.mappers.LegalAccountMapper;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;
import ru.stepagin.becoder.service.PersonService;
import ru.stepagin.becoder.service.SecurityService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("${api.endpoints.base-url}/accounts")
@RequiredArgsConstructor
@Tag(name = "API управления балансом")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно выполнено"),
        @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                content = @Content(schema = @Schema(implementation = String.class)))
})
public class BalanceController {
    private final LegalAccountService accountService;
    private final AccessService accessService;
    private final SecurityService securityService;
    private final PersonService personService;

    @Operation(summary = "Создать юридический счёт")
    @PostMapping
    public ResponseEntity<LegalAccountDto> createAccount(Authentication authentication) {
        PersonEntity person = securityService.getPerson(authentication);
        LegalAccountDto account = accountService.createAccount(person);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Показать список доступных счетов")
    @GetMapping
    public ResponseEntity<List<LegalAccountDto>> getAllAccounts(Authentication auth) {
        PersonEntity person = securityService.getPerson(auth);
        return ResponseEntity.ok(accessService.getAllByPerson(person));
    }

    @Operation(summary = "Показать данные счёта")
    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    public ResponseEntity<LegalAccountDto> getAccountDetails(@PathVariable String id) {
        LegalAccountDto account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Пополнить счёт")
    @PostMapping("/{accountId}/deposit")
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    public ResponseEntity<LegalAccountDto> increaseAccountBalance(@PathVariable(name = "accountId") String id,
                                                                  @RequestBody @Validated BalanceChangeDto balanceChange) {
        LegalAccountEntity account = accountService.getAccountEntityById(id);
        accountService.increaseBalance(account, balanceChange.getAmount());
        account.setBalance(account.getBalance() + balanceChange.getAmount());
        return ResponseEntity.ok(LegalAccountMapper.toDto(account));
    }

    @Operation(summary = "Вывести со счёта")
    @PostMapping("/{accountId}/withdrawal")
    @PreAuthorize("@securityService.hasAccessToAccount(#id, authentication)")
    public ResponseEntity<LegalAccountDto> decreaseAccountBalance(@PathVariable(name = "accountId") String id,
                                                                  @RequestBody @Validated BalanceChangeDto balanceChange) {
        LegalAccountEntity account = accountService.isEnough(id, balanceChange.getAmount());
        accountService.decreaseBalance(account, balanceChange.getAmount());
        account.setBalance(account.getBalance() - balanceChange.getAmount());
        return ResponseEntity.ok(LegalAccountMapper.toDto(account));

    }

    @Operation(summary = "Получить список пользователей, имеющих доступ к счёту")
    @GetMapping("/{accountId}/partners")
    @PreAuthorize("@securityService.isActiveOwner(#accountId, authentication)")
    public ResponseEntity<List<PersonDto>> getAllPartners(@PathVariable String accountId) {
        return ResponseEntity.ok(accessService.getPartnersByAccountId(accountId));
    }

    @Operation(summary = "Выдать пользователю доступ к счёту")
    @PutMapping("/{accountId}/partners")
    @PreAuthorize("@securityService.isActiveOwner(#accountId, authentication)")
    public ResponseEntity<String> grantAccessToAccount(@PathVariable(name = "accountId") String accountId,
                                                       @RequestBody @Validated PersonDto partner) {
        accessService.grantAccess(accountId, partner.getLogin());
        return ResponseEntity.ok("Пользователю " + partner.getLogin()
                + " выдан доступ к аккаунту " + accountId);
    }

    @Operation(summary = "Отозвать доступ к счёту у пользователя")
    @DeleteMapping("/{accountId}/partners/{partner}")
    @PreAuthorize("@securityService.isActiveOwner(#accountId, authentication)")
    public ResponseEntity<String> revokeAccessFromAccount(@PathVariable(name = "accountId") String accountId,
                                                          @PathVariable(name = "partner") String partnerLogin) {
        accessService.revokeAccess(accountId, partnerLogin);
        return ResponseEntity.ok("У пользователя " + personService.getUser(partnerLogin).getLogin()
                + " отозван доступ к аккаунту " + accountId);
    }
}
