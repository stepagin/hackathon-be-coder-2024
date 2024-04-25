package ru.stepagin.becoder.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.LegalAccountEntity;

import java.util.UUID;

@Repository
public interface LegalAccountRepository extends JpaRepository<LegalAccountEntity, UUID> {

    @Modifying
    @Transactional
    @Query("update LegalAccountEntity l set l.balance = l.balance+:p where l.id = :id")
    void increaseBalanceById( @Param("id") UUID id, @Param("p") Long amount);


    @Modifying
    @Transactional
    @Query("update LegalAccountEntity l set l.balance = l.balance-:p where l.id = :id")
    void decreaseBalanceById(@Param("id") UUID id, @Param("p") Long amount);

}
