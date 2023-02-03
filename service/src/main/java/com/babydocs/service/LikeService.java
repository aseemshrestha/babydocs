package com.babydocs.service;

import com.babydocs.model.Likes;
import com.babydocs.repository.LikeRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Data
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional(rollbackFor = Exception.class)
    public Likes saveLike(Likes likes) {
        return likeRepository.save(likes);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLikeById(Long id) {
        this.likeRepository.deleteByLikeId(id);
    }
}
