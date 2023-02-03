package com.babydocs.controller;

import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Comment;
import com.babydocs.model.CommentDTO;
import com.babydocs.model.MediaComment;
import com.babydocs.model.MediaCommentDTO;
import com.babydocs.model.MediaFiles;
import com.babydocs.model.Post;
import com.babydocs.service.CommentService;
import com.babydocs.service.MediaService;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class CommentController {

    private final CommentService commentService;
    private final PostStorageService postStorageService;
    private final MediaService mediaService;


    /**
     * Posts comment to a particular post.
     */
    @PostMapping("v1/secured/post-comment")
    public ResponseEntity<Comment> createComment(@RequestBody @Valid CommentDTO comment,
                                                 HttpServletRequest request) {
        String loggedInUser = request.getUserPrincipal().getName();
        Post postById = postStorageService
                .getPostById(comment.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        var comm = new Comment();
        comm.setComment(comment.getComment());
        comm.setCommentedBy(loggedInUser);
        comm.setCreated(new Date());
        comm.setLastUpdated(new Date());
        comm.setPost(postById);
        final Comment savedComment = this.commentService.saveComment(comm);
        int commentCount = postById.getCommentCount();
        int updatedCount = commentCount + 1;
        postStorageService.updateCommentCount(updatedCount, postById.getId());
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    /*
      Should return comment by postId.
     */
    @GetMapping("v1/secured/get-comments/{postId}")
    public ResponseEntity<List<Comment>> getCommentByPostId(@PathVariable(value = "postId") @NotNull Long postId) {
        List<Comment> commentsByPostId = this.commentService.getCommentsByPostId(postId);
        if (commentsByPostId.isEmpty()) {
            throw new ResourceNotFoundException("Post with " + postId + " not found");
        }
        return new ResponseEntity<>(commentsByPostId, HttpStatus.OK);
    }

    /*
       Posts comment to media like images which is a part of the post.
     */
    @PostMapping("v1/secured/post-comment-media")
    public ResponseEntity<MediaComment> createCommentMedia(@RequestBody @Valid MediaCommentDTO media,
                                                           HttpServletRequest request) {
        String loggedInUser = request.getUserPrincipal().getName();
        MediaFiles mediaById = mediaService.getMediaById(media.getMediaId());
        if (mediaById == null) {
            throw new ResourceNotFoundException("Post with media id " + media.getMediaId() + " not found");
        }
        var mediaComment = new MediaComment();
        mediaComment.setComment(media.getComment());
        mediaComment.setCommentedBy(loggedInUser);
        mediaComment.setLastUpdated(new Date());
        mediaComment.setCreated(new Date());
        mediaComment.setMedia(mediaById);
        final MediaComment savedComment = this.mediaService.saveMediaComment(mediaComment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    /*
      Should return media comments like comments on images.
     */
    @GetMapping("v1/secured/get-media-comments/{mediaId}")
    public ResponseEntity<List<MediaComment>> getCommentsByMediaId(@PathVariable(value = "mediaId") @NotNull Long mediaId) {
        List<MediaComment> mediaCommentsByMediaId = this.mediaService.getMediaCommentsByMediaId(mediaId);
        if (mediaCommentsByMediaId.isEmpty()) {
            throw new ResourceNotFoundException("Post with " + mediaId + " not found");
        }
        return new ResponseEntity<>(mediaCommentsByMediaId, HttpStatus.OK);
    }
}
