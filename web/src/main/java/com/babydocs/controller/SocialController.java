package com.babydocs.controller;

import com.babydocs.Constants.PostType;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Post;
import com.babydocs.model.SwitchPostVisibilityDTO;
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
import java.util.Date;

@RestController
@Data
@RequestMapping("api/")
public class SocialController {

    private final PostStorageService postStorageService;
    private final UserValidationService userValidationService;

    @PatchMapping("v1/secured/switch-post-visibility")
    public ResponseEntity<Post> switchPostVisibility(@RequestBody @Valid SwitchPostVisibilityDTO dto, HttpServletRequest request) throws Exception {
        Post postById = postStorageService.getPostById(dto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        final String username = request.getUserPrincipal().getName();
        userValidationService.isLoggedUserValid(username, request);
        PostType[] postTypes = PostType.values();
        boolean isValidPostType = false;
        for (PostType accType : postTypes) {
            if (accType.name().equals(dto.postType)) {
                isValidPostType = true;
                break;
            }
        }
        if (!isValidPostType) {
            throw new BadRequestException("Not Valid Account Type");
        }
        postById.setPostType(dto.getPostType());
        postById.setLastUpdated(new Date());
        Post updatedPost = postStorageService.savePosts(postById);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);

    }
}
