package com.babydocs.controller;

import com.babydocs.constants.PostType;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Activity;
import com.babydocs.model.Likes;
import com.babydocs.model.Post;
import com.babydocs.model.SwitchPostVisibilityDTO;
import com.babydocs.service.ActivityService;
import com.babydocs.service.LikeService;
import com.babydocs.service.PostStorageService;
import com.babydocs.service.ValidationService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Data
@RequestMapping("api/")
public class SocialController {

    private final PostStorageService postStorageService;
    private final ValidationService validationService;
    private final ActivityService activityService;
    private final LikeService likeService;

    @PatchMapping("v1/secured/switch-post-type")
    public ResponseEntity<Post> switchPostVisibility(@RequestBody @Valid SwitchPostVisibilityDTO dto, HttpServletRequest request) throws Exception {
        Post postById = postStorageService.getPostById(dto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        String existingPostType = postById.getPostType();
        final String username = request.getUserPrincipal().getName();
        validationService.isLoggedUserValid(username, request);
        PostType[] postTypes = PostType.values();
        boolean isValidPostType = Arrays.stream(postTypes).anyMatch(accType -> accType.name().equals(dto.postType));
        assert isValidPostType : "Not Valid Account Type";
        postById.setPostType(dto.getPostType());
        postById.setLastUpdated(new Date());
        var updatedPost = postStorageService.savePosts(postById);
        var activity = new Activity();
        activity.setPostId(updatedPost.getId());
        activity.setEventDate(new Date());
        activity.setMessage("Post type is switched from " + existingPostType + " to " + updatedPost.getPostType());
        activity.setEventOwner(username);
        activityService.saveActivity(activity);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PostMapping("v1/secured/like-post/{postId}")
    public ResponseEntity<Likes> doLike(@PathVariable("postId") Long postId,
                                        HttpServletRequest request) {
        String loggedInUser = request.getUserPrincipal().getName();
        Post post = postStorageService.getPostById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        List<Likes> likes = post.getLikes();
        for (Likes _like : likes) {
            if (_like.getLikedBy().equals(loggedInUser)) {
                likeService.deleteLikeById(_like.getId());
                int _likedCount = post.getLikeCount();
                AtomicInteger _updatedCount = new AtomicInteger(_likedCount - 1);
                postStorageService.updateLikeCount(_updatedCount.intValue(), postId);
                var activity = new Activity();
                activity.setPostId(postId);
                activity.setEventDate(new Date());
                activity.setMessage("You unliked a post " + post.getTitle());
                activity.setEventOwner(loggedInUser);
                activityService.saveActivity(activity);
                return new ResponseEntity<>(_like, HttpStatus.OK);
            }
        }
        var like = new Likes();
        like.setPost(post);
        like.setLikedBy(loggedInUser);
        like.setLastUpdated(new Date());
        like.setLikedOn(new Date());
        Likes savedLike = this.likeService.saveLike(like);
        int likedCount = post.getLikeCount();
        AtomicInteger updatedCount = new AtomicInteger(likedCount + 1);
        postStorageService.updateLikeCount(updatedCount.intValue(), postId);
        var activity = new Activity();
        activity.setPostId(postId);
        activity.setEventDate(new Date());
        activity.setMessage("You liked a post " + post.getTitle());
        activity.setEventOwner(loggedInUser);
        activityService.saveActivity(activity);
        return new ResponseEntity<>(savedLike, HttpStatus.CREATED);
    }
}
