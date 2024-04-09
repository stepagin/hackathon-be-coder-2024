package ru.stepagin.becoder.component;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;
import ru.stepagin.becoder.service.PersonService;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final PersonRepository personRepository;

    public SetupDataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
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
    }
}
