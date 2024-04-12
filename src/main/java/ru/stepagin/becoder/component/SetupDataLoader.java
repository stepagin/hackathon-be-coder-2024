package ru.stepagin.becoder.component;

import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.LegalAccountEntity;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.AccessRepository;
import ru.stepagin.becoder.repository.LegalAccountRepository;
import ru.stepagin.becoder.repository.PersonRepository;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final PersonRepository personRepository;

    private final LegalAccountRepository legalAccountRepository;

    private final AccessRepository accessRepository;

    public SetupDataLoader(PersonRepository personRepository, LegalAccountRepository legalAccountRepository, AccessRepository accessRepository) {
        this.personRepository = personRepository;
        this.legalAccountRepository = legalAccountRepository;
        this.accessRepository = accessRepository;
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

//        LegalAccountDTO dto = accountService.createAccount();
        LegalAccountEntity account = new LegalAccountEntity(0L);
        account = legalAccountRepository.save(account);
//        account.setId(UUID.fromString(dto.getId()));
//        account.setBalance(dto.getBalance());

        AccessEntity access = new AccessEntity();
        access.setPerson(admin);
        access.setAccount(account);
        accessRepository.save(access);


    }
}