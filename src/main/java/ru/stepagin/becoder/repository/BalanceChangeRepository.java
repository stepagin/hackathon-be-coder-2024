package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.BalanceChangeEntity;

@Repository
public interface BalanceChangeRepository extends JpaRepository<BalanceChangeEntity, Long> {
}
