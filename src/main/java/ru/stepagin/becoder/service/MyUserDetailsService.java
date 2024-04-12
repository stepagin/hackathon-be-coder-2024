package ru.stepagin.becoder.service;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.stepagin.becoder.entity.PersonEntity;
import ru.stepagin.becoder.microservicesConnection.Message;

@Service
@Component
@EnableJms
public class MyUserDetailsService implements UserDetailsService {
    private final JmsTemplate jmsTemplate;
    private final String queueName = "Person";

    public MyUserDetailsService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }



    //TODO: возможно будут проблемы с методом из PersonService, надо проверить
    @Override
    @JmsListener(destination = queueName+"GetByLogin")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        PersonEntity user = personRepository.findByLogin(username); //username = email
        Message message = new Message(new PersonEntity(username));
        Message response = (Message) jmsTemplate.sendAndReceive(queueName, session -> session.createObjectMessage(message));
        assert response != null;
        PersonEntity user = response.getPerson();
        if (user == null) throw new UsernameNotFoundException("нет такого пользователя");
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .build();
    }
}
