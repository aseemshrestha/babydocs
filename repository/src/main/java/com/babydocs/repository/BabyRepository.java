package com.babydocs.repository;

import com.babydocs.model.Baby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface BabyRepository extends JpaRepository<Baby, Long> {
    @Query("SELECT b from Baby b where b.user.username = :username")
    Optional<List<Baby>> findBabyByUsername(String username);
}

