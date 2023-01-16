package com.babydocs.controller;

import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Comment;
import com.babydocs.model.CommentDTO;
import com.babydocs.model.Post;
import com.babydocs.service.CommentService;
import com.babydocs.service.PostStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class CommentController {

    private final CommentService commentService;
    private final PostStorageService postStorageService;

    @PostMapping("v1/secured/post-comment")
    public ResponseEntity<Comment> createComment(@RequestBody CommentDTO comment,
                                                 HttpServletRequest request) {
        String loggedInUser = request.getUserPrincipal().getName();
        Post postById = postStorageService.getPostById(comment.getPostId());
        if (postById == null) {
            throw new ResourceNotFoundException("Post with is {} not found" + comment.getPostId().toString());
        }
        var comm = new Comment();
        comm.setComment(comment.getComment());
        comm.setCommentedBy(loggedInUser);
        comm.setCreated(new Date());
        comm.setLastUpdated(new Date());
        comm.setPost(postById);
        final Comment savedComment = this.commentService.saveComment(comm);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    @GetMapping("v1/secured/get-comments/{postId}")
    public ResponseEntity<List<Comment>> getCommentByPostId(@PathVariable(value = "postId") @NotNull Long postId) {
        List<Comment> commentsByPostId = this.commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(commentsByPostId, HttpStatus.OK);
    }
}
