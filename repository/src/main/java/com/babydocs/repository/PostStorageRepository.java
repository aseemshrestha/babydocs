package com.babydocs.repository;


import com.babydocs.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostStorageRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from Post p where p.postedBy = :username order by p.lastUpdated desc")
    Optional<List<Post>> findPostByUsername(String username);

    @Query("SELECT p from Post p where p.id = :id")
    Optional<Post> findPostById(Long id);

}

