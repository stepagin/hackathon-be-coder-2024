package ru.stepagin.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepagin.becoder.entity.PersonEntity;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    @Query("select p from PersonEntity p where upper(p.login) = upper(?1)")
    PersonEntity findByLogin(String login);


    boolean existsByLoginAllIgnoreCase(String login);
}
