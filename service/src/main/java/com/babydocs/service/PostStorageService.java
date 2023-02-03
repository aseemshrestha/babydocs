package com.babydocs.service;

import com.babydocs.model.Post;
import com.babydocs.repository.PostStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostStorageService {

    private final PostStorageRepository postStorageRepository;

    public PostStorageService(PostStorageRepository postStorageRepository) {
        this.postStorageRepository = postStorageRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Post savePosts(Post post) {
        return this.postStorageRepository.save(post);
    }

    public Optional<List<Post>> getPosts(String username) {
        return postStorageRepository.findPostByUsername(username);
    }

    public Optional<Post> getPostById(Long id) {
        return this.postStorageRepository.findPostById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        this.postStorageRepository.deleteById(id);
    }

    public void updateCommentCount(int commentCount, Long id) {
        this.postStorageRepository.updateCommentCount(commentCount, id);
    }

    public void updateLikeCount(int likeCount, Long id) {
        this.postStorageRepository.updateLikeCount(likeCount, id);
    }
}
