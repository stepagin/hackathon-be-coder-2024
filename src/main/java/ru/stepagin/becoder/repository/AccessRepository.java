package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.AccessEntity;
import ru.stepagin.becoder.entity.PersonEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {
    AccessEntity findByAccount_IdAndPersonId(UUID accountId, Long personId);

    AccessEntity findByAccount_IdAndPerson_LoginIgnoreCase(UUID id, String login);


    List<AccessEntity> findByPerson(PersonEntity person);

    List<AccessEntity> findByAccount_Id(UUID id);
}
