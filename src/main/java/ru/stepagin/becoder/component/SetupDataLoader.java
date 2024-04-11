package ru.stepagin.becoder.component;

import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;
import ru.stepagin.becoder.service.AccessService;
import ru.stepagin.becoder.service.LegalAccountService;

import java.util.UUID;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final PersonRepository personRepository;

    private final LegalAccountService accountService;

    private final AccessService accessService;

    public SetupDataLoader(PersonRepository personRepository, LegalAccountService accountService, AccessService accessService) {
        this.personRepository = personRepository;
        this.accountService = accountService;
        this.accessService = accessService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;
        PersonEntity admin = new PersonEntity();
        admin.setLogin("admin");
        admin.setPassword("$2a$12$LmIt5MN8Vd2tzaGKtV4oTuRTPoSZXjaz6vqNP9c7ABirQonUWj1hG");
        personRepository.save(admin);
        alreadySetup = true;

        LegalAccountDTO dto = accountService.createAccount();
        LegalAccountEntity account = new LegalAccountEntity();
        account.setId(UUID.fromString(dto.getId()));
        account.setBalance(dto.getBalance());

        AccessEntity access = new AccessEntity();
        access.setPerson(admin);
        access.setAccount(account);
        accessService.save(access);


    }
}
