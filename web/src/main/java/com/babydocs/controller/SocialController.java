package com.babydocs.controller;

import com.babydocs.Constants.PostType;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Activity;
import com.babydocs.model.Post;
import com.babydocs.model.SwitchPostVisibilityDTO;
import com.babydocs.service.ActivityService;
import com.babydocs.service.PostStorageService;
import com.babydocs.service.UserValidationService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;

@RestController
@Data
@RequestMapping("api/")
public class SocialController {

    private final PostStorageService postStorageService;
    private final UserValidationService userValidationService;

    private final ActivityService activityService;

    @PatchMapping("v1/secured/switch-post-type")
    public ResponseEntity<Post> switchPostVisibility(@RequestBody @Valid SwitchPostVisibilityDTO dto, HttpServletRequest request) throws Exception {
        Post postById = postStorageService.getPostById(dto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        String existingPostType = postById.getPostType();
        final String username = request.getUserPrincipal().getName();
        userValidationService.isLoggedUserValid(username, request);
        PostType[] postTypes = PostType.values();
        boolean isValidPostType = Arrays.stream(postTypes).anyMatch(accType -> accType.name().equals(dto.postType));
        assert isValidPostType : "Not Valid Account Type";
        postById.setPostType(dto.getPostType());
        postById.setLastUpdated(new Date());
        var updatedPost = postStorageService.savePosts(postById);
        // send notification to kafka - todo
        var activity = new Activity();
        activity.setPostId(updatedPost.getId());
        activity.setEventDate(new Date());
        activity.setMessage("Post type is switched from " + existingPostType + " to " + updatedPost.getPostType());
        activity.setEventOwner(username);
        activityService.saveActivity(activity);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);

    }
}
