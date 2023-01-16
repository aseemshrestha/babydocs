package com.babydocs.controller;

import com.babydocs.Constants;
import com.babydocs.config.AWSConfig;
import com.babydocs.exceptions.BadRequestException;
import com.babydocs.model.Baby;
import com.babydocs.model.ImageDeleteDTO;
import com.babydocs.model.MediaFiles;
import com.babydocs.model.Post;
import com.babydocs.model.User;
import com.babydocs.service.AWSS3Service;
import com.babydocs.service.BabyService;
import com.babydocs.service.MediaService;
import com.babydocs.service.PostStorageService;
import com.babydocs.service.UserAndRoleService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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


    @PostMapping("v1/secured/submit-baby-details")
    public ResponseEntity<Baby> submitBabyDetails(@RequestBody @Valid Baby baby, HttpServletRequest request) {
        baby.setLastUpdated(new Date());
        baby.setCreated(new Date());
        User user = userAndRoleService.getUser(request.getUserPrincipal().getName()).get();
        baby.setUser(user);
        Baby savedBaby = this.babyService.saveBaby(baby);
        return new ResponseEntity<>(savedBaby, HttpStatus.OK);
    }


    @PostMapping("v1/secured/submit-post")
    public ResponseEntity<Post> createPost(@RequestParam(value = "title") String title,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "file", required = false) MultipartFile[] files,
                                           @RequestParam(value = "album", required = false) String album,
                                           HttpServletRequest request) {

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

    @PostMapping("v1/secured/delete-image")
    public ResponseEntity<String> deleteImage(@RequestBody ImageDeleteDTO[] imageDeleteDto) throws Exception {
        var filesToDelete = new String[imageDeleteDto.length];
        var idsToDelete = new Long[imageDeleteDto.length];

        for (int i = 0; i < imageDeleteDto.length; i++) {
            filesToDelete[i] = imageDeleteDto[i].getMediaLocation().split("/")[3];
            idsToDelete[i] = imageDeleteDto[i].getMediaId();
        }

        int deletedFiles = awss3Service.deleteFiles(filesToDelete);
        if (deletedFiles > 0) {
            Arrays.stream(idsToDelete).forEach(this.mediaService::deleteImageById);
        }
        return new ResponseEntity<>("Successfully Deleted", HttpStatus.OK);

    }
}
