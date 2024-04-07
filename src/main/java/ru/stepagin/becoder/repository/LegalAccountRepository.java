package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Repository
public interface LegalAccountRepository extends JpaRepository<LegalAccountEntity, Long> {
}
