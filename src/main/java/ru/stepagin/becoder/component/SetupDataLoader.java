//package ru.stepagin.becoder.component;
//
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Component;
//import ru.stepagin.becoder.entity.PersonEntity;
//import ru.stepagin.becoder.microservicesConnection.Message;
//
//@Component
//public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
//
//    boolean alreadySetup = false;
//
//    private final JmsTemplate jmsTemplate;
//    private final String queueName = "Person";
//
//
//    public SetupDataLoader(JmsTemplate jmsTemplate) {
//        this.jmsTemplate = jmsTemplate;
//    }
//
//    @Override
//    @Transactional
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if (alreadySetup) return;
//        PersonEntity admin = new PersonEntity();
//        admin.setLogin("admin");
//        admin.setPassword("$2a$12$LmIt5MN8Vd2tzaGKtV4oTuRTPoSZXjaz6vqNP9c7ABirQonUWj1hG");
//        Message message = new Message(admin);
//        jmsTemplate.convertAndSend(queueName + "Save", message);
////        personRepository.save(admin);
//        alreadySetup = true;
//    }
//}
