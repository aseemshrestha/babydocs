package com.babydocs.controller;

import com.babydocs.Constants;
import com.babydocs.config.AWSConfig;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.exceptions.ResourceNotFoundException;
import com.babydocs.model.Baby;
import com.babydocs.model.Comment;
import com.babydocs.model.ImageDeleteDTO;
import com.babydocs.model.MediaFiles;
import com.babydocs.model.Post;
import com.babydocs.model.User;
import com.babydocs.service.AWSS3Service;
import com.babydocs.service.BabyService;
import com.babydocs.service.CommentService;
import com.babydocs.service.MediaService;
import com.babydocs.service.PostStorageService;
import com.babydocs.service.UserAndRoleService;
import com.babydocs.service.UserValidationService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
@Data
@Slf4j
public class DashboardController {
    private final UserAndRoleService userAndRoleService;
    private final BabyService babyService;
    private final PostStorageService postStorageService;
    private final AWSS3Service awss3Service;
    private final MediaService mediaService;
    private final UserValidationService userValidationService;
    private final CommentService commentService;


    /*
      Submit baby details. Child registration is mandatory to submit posts.
     */
    @PostMapping("v1/secured/submit-baby-details")
    public ResponseEntity<Baby> submitBabyDetails(@RequestBody @Valid Baby baby, HttpServletRequest request) {
        baby.setLastUpdated(new Date());
        baby.setCreated(new Date());
        User user = userAndRoleService.getUser(request.getUserPrincipal().getName()).get();
        baby.setUser(user);
        Baby savedBaby = this.babyService.saveBaby(baby);
        return new ResponseEntity<>(savedBaby, HttpStatus.OK);
    }


    /*
       Submits post. Media like images are optional. User must register child account to submit the post.
       By default, post are set as private, visible only to the user who submits the post.
     */
    @PostMapping("v1/secured/submit-post")
    public ResponseEntity<Post> createPost(@RequestParam(value = "title") String title, @RequestParam(value = "description", required = false) String description, @RequestParam(value = "file", required = false) MultipartFile[] files, @RequestParam(value = "album", required = false) String album, HttpServletRequest request) {

        long start = System.currentTimeMillis();
        Optional<List<Baby>> babyByUserName = babyService.findBabyByUserName(request.getUserPrincipal().getName());
        boolean empty = babyByUserName.get().isEmpty();
        if (empty) {
            throw new BadRequestException("Please register baby to post");
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        var post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setAlbumName(album);

        String username = request.getUserPrincipal().getName();
        String path = username.substring(0, username.indexOf('@'));

        post.setIp(ip);
        post.setBrowser(userAgent.getBrowser() + "-" + userAgent.getOperatingSystem());
        post.setCreated(new Date());
        post.setLastUpdated(new Date());

        post.setPostedBy(request.getUserPrincipal().getName());
        post.setPostType(Constants.PostType.PRIVATE.name());
        if (files != null) {
            List<String> uploadedFiles = awss3Service.uploadFileList(files, path);
            List<MediaFiles> mediaList = new ArrayList<>();

            uploadedFiles.forEach(f -> {
                MediaFiles media = new MediaFiles();
                media.setMediaType("image");
                media.setMediaLocation(AWSConfig.getS3EndPoint() + f);
                media.setMediaDescription(description);
                media.setCreated(new Date());
                media.setLastUpdated(new Date());
                media.setPost(post);
                mediaList.add(media);
            });
            post.setMediaFiles(mediaList);
        }
        Post savedPost = postStorageService.savePosts(post);
        long end = System.currentTimeMillis();
        log.info("time elapsed in creating a post ================ " + (start - end));
        return new ResponseEntity<>(savedPost, HttpStatus.OK);

    }

    /*
       Deletes images from s3. If image is deleted, should delete the related s3 location entry in database as well.
       Only the user who submitted the post can delete his/her images.
     */
    @DeleteMapping("v1/secured/delete-my-media")
    public ResponseEntity<String> deleteImage(@RequestBody @Valid ImageDeleteDTO[] imageDeleteDto, HttpServletRequest request) throws Exception {

        MediaFiles mediaFile = mediaService.getMediaById(imageDeleteDto[0].getMediaId());
        if (!mediaFile.getPost().getPostedBy().equals(request.getUserPrincipal().getName())) {
            throw new BadRequestException("You are not authorized to perform delete operation.");
        }
        var filesToDelete = new String[imageDeleteDto.length];
        var idsToDelete = new Long[imageDeleteDto.length];

        for (int i = 0; i < imageDeleteDto.length; i++) {
            filesToDelete[i] = imageDeleteDto[i].getMediaLocation().split("/")[3];
            idsToDelete[i] = imageDeleteDto[i].getMediaId();
        }

        int deletedFiles = awss3Service.deleteFiles(filesToDelete);
        if (deletedFiles > 0) {
            Arrays.stream(idsToDelete).forEach(this.mediaService::deleteMedia);
        }
        return new ResponseEntity<>("Successfully Deleted", HttpStatus.OK);

    }

    /*
     * Fetches logged-in user's post only. Should not fetch other users post.
     */
    @GetMapping("v1/secured/get-my-posts/{username}")
    public ResponseEntity<?> getMyPosts(@PathVariable("username") String username, HttpServletRequest request) throws Exception {
        userValidationService.isLoggedUserValid(username, request);
        Optional<List<Post>> posts = postStorageService.getPosts(username);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /*
     *  Deletes a post of a user. Only the user who submitted the post can delete his/her post.
     *  If the post is deleted, all comments, media uploads related to the post should be deleted.
     */
    @DeleteMapping("v1/secured/delete-my-post/{postId}")
    public ResponseEntity<?> deleteMyPost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        Post postById = this.postStorageService.getPostById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (postById.getPostedBy().equals(request.getUserPrincipal().getName())) {
            this.postStorageService.deletePost(postId);
        } else {
            throw new BadRequestException("You are not authorized to perform delete operation.");
        }
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    /*
     *  Deletes comment of a main post. Only owner of the post or the user who commented, should be permitted
     *  to delete the comment.
     */
    @DeleteMapping("v1/secured/delete-main-post-comment")
    public ResponseEntity<?> deletePostComment(@RequestParam(value = "commentId") Long commentId,
                                               HttpServletRequest request) {
        String loggedInUsername = request.getUserPrincipal().getName();
        Optional<Comment> commentByCommentId = this.commentService.getCommentByCommentId(commentId);
        if (commentByCommentId.isEmpty()) {
            throw new ResourceNotFoundException("Comment Not Found");
        }
        String postedBy = commentByCommentId.get().getPost().getPostedBy();
        if (postedBy.equals(loggedInUsername)) {
            this.commentService.deleteComment(commentId);
        } else {
            Comment comment = this.commentService.getCommentByUsernameAndCommentId(loggedInUsername, commentId);
            if (comment == null) {
                throw new BadRequestException("You are not authorized to perform delete operation.");
            }
            this.commentService.deleteComment(comment.getId());
        }
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    /*
     Updates the main post texts.
    */
    @PatchMapping("v1/secured/edit-post-text")
    public ResponseEntity<?> updatePost(
            @RequestParam(value = "postId") Long postId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "album", required = false) String album, HttpServletRequest request) throws Exception {

        Post post = postStorageService.getPostById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        userValidationService.isLoggedUserValid(post.getPostedBy(), request);
        if (StringUtils.isNotEmpty(title)) {
            post.setTitle(title);
        }
        if (StringUtils.isNotEmpty(description)) {
            post.setDescription(description);
        }
        if (StringUtils.isNotEmpty(album)) {
            post.setAlbumName(album);
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        post.setIp(ip);
        post.setLastUpdated(new Date());
        Post savedPost = postStorageService.savePosts(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    /*
      Add additional media ( images ) to existing post
     */
    @PutMapping("v1/secured/add-image-to-existing-album")
    public ResponseEntity<Post> addAdditionMediaToExistingPost(@RequestParam(value = "postId") Long postId,
                                                               @RequestParam(value = "file") MultipartFile[] files,
                                                               HttpServletRequest request) throws Exception {
        Post post = postStorageService.getPostById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        String username = request.getUserPrincipal().getName();
        userValidationService.isLoggedUserValid(username, request);
        String path = username.substring(0, username.indexOf('@'));

        if (files != null) {
            List<String> uploadedFiles = awss3Service.uploadFileList(files, path);
            List<MediaFiles> mediaList = new ArrayList<>();

            uploadedFiles.forEach(f -> {
                MediaFiles media = new MediaFiles();
                media.setMediaLocation(AWSConfig.getS3EndPoint() + f);
                media.setMediaType("image");
                media.setMediaDescription(post.getDescription());
                media.setCreated(new Date());
                media.setLastUpdated(new Date());
                media.setPost(post);
                mediaList.add(media);
            });
            post.setMediaFiles(mediaList);
        }
        Post savedPost = postStorageService.savePosts(post);
        return new ResponseEntity<>(savedPost, HttpStatus.OK);

    }

}



