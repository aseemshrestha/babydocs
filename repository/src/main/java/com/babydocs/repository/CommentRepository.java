package com.babydocs.repository;

import com.babydocs.model.Comment;
import com.babydocs.model.MediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c from Comment c where c.post.id = :postId order by c.created desc")
    List<Comment> getCommentByPostId(Long postId);

    // select * from comment where commented_by = '' and id= '';
    @Query("SELECT c from Comment c where c.commentedBy = :username and c.id = :commentId")
    Comment getCommentByUsernameAndCommentId(String username, Long commentId);

}
