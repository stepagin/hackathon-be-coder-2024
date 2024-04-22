package ru.stepagin.becoder.component;

import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.DTO.LegalAccountDTO;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;
import ru.stepagin.becoder.service.LegalAccountService;

import java.util.UUID;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final PersonRepository personRepository;

    private final LegalAccountService accountService;

    public SetupDataLoader(PersonRepository personRepository, LegalAccountService accountService) {
        this.personRepository = personRepository;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;
        PersonEntity admin = new PersonEntity();
        admin.setLogin("admin");
        admin.setPassword("$2a$12$LmIt5MN8Vd2tzaGKtV4oTuRTPoSZXjaz6vqNP9c7ABirQonUWj1hG");
        admin = personRepository.save(admin);
        alreadySetup = true;

        LegalAccountDTO dto = accountService.createAccount(admin);
        LegalAccountEntity account = new LegalAccountEntity();
        account.setId(UUID.fromString(dto.getId()));
        account.setBalance((long) dto.getBalance());


    }
}
