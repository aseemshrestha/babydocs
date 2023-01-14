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
}
