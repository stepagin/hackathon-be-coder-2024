package ru.stepagin.becoder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

import java.util.Objects;

@Service
public class SecurityService {
    final
    PersonRepository personRepository;

    public SecurityService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public boolean isAuthorized(Long id, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        PersonEntity person = personRepository.findByLogin(user.getUsername());
        if(person == null) return false;
        return person.getId() != null && person.getId().equals(id);
    }

}
