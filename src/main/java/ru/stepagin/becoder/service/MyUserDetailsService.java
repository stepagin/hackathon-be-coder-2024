package ru.stepagin.becoder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.repository.PersonRepository;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PersonEntity user = personRepository.findByLogin(username); //username = email
        if (user == null) throw new UsernameNotFoundException("нет такого пользователя");
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .build();
    }
}
