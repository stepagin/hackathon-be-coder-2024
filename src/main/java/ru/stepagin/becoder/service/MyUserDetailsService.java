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

import static java.lang.Thread.sleep;

@Service
@Component
@EnableJms
public class MyUserDetailsService implements UserDetailsService {
    private final JmsTemplate jmsTemplate;
    private final String queueName = "Person";

    public MyUserDetailsService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Message request = new Message(new PersonEntity(username));
        jmsTemplate.convertAndSend(queueName + "GetByLoginRequest", request);
        Message response = (Message) jmsTemplate.receiveAndConvert(queueName + "GetByLoginResponse");
        PersonEntity user = response.getPerson();
        if (user == null) throw new UsernameNotFoundException("нет такого пользователя");
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .build();
    }
}
