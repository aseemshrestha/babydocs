package com.babydocs.repository;

import com.babydocs.model.Baby;
import com.babydocs.model.Comment;
import com.babydocs.model.MediaComment;
import com.babydocs.model.MediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<MediaFiles, Long> {
    @Modifying
    @Query("DELETE from MediaFiles m where m.mediaLocation = :mediaLocation")
    void deleteByMediaLocation(String mediaLocation);

    @Modifying
    @Query("DELETE from MediaFiles m where m.id = :id")
    void deleteByMediaId(Long id);


}