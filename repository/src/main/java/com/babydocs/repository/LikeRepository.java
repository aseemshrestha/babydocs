package com.babydocs.repository;

import com.babydocs.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    @Modifying
    @Query("DELETE from Likes l where l.id = :id")
    void deleteByLikeId(Long id);
    //test comment
}
