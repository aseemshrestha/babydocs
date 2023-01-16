package com.babydocs.service;

import com.babydocs.model.Comment;
import com.babydocs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(rollbackFor = Exception.class)
    public Comment saveComment(Comment comment) {
        return this.commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return this.commentRepository.getCommentByPostId(postId);
    }
}
