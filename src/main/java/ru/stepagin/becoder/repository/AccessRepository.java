package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.AccessEntity;

@Repository
public interface AccessRepository extends JpaRepository<AccessEntity, Long> {
    public AccessEntity findByAccount_IdAndPersonId(Long accountId, Long personId);
}
