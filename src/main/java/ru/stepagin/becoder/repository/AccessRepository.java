package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.AccessEntity;

import java.util.UUID;

@Repository
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {
    AccessEntity findByAccount_IdAndPersonId(UUID accountId, Long personId);
}
