package com.babydocs.repository;

import com.babydocs.model.MediaComment;
import com.babydocs.model.MediaFiles;
import com.babydocs.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaCommentRepository extends JpaRepository<MediaComment, Long> {
    @Query("SELECT m from MediaComment m where m.media.id = :mediaId order by m.created desc")
    List<MediaComment> getMediaCommentsByMediaId(Long mediaId);

    @Query("SELECT m from MediaFiles m where m.id = :id")
    MediaFiles findMediaById(Long id);
}