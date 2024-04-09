package ru.stepagin.becoder.repository;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.LegalAccountEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LegalAccountRepository extends JpaRepository<LegalAccountEntity, UUID> {
    @Modifying
    @Transactional
    @Query("update LegalAccountEntity a set a.balance = ?2 where a.id = ?1")
    void updateBalanceByAccountId(UUID accountId, Long balance);
}
