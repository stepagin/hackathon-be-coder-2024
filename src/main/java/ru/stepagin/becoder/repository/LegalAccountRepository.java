package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.LegalAccountEntity;

@Repository
public interface LegalAccountRepository extends JpaRepository<LegalAccountEntity, Long> {
    @Query("update LegalAccountEntity a set a.balance = ?2 where a.id = ?1")
    void updateBalance(Long accountId, Long balance);
}
