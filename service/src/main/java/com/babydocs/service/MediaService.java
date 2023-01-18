package com.babydocs.service;

import com.babydocs.model.MediaComment;
import com.babydocs.model.MediaFiles;
import com.babydocs.repository.MediaCommentRepository;
import com.babydocs.repository.MediaRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class MediaService {
    private final MediaRepository mediaRepository;
    private final MediaCommentRepository mediaCommentRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageById(Long id) {
        this.mediaRepository.deleteByMediaId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageByMediaLocation(String mediaLocation) {
        this.mediaRepository.deleteByMediaLocation(mediaLocation);
    }

    public MediaFiles getMediaById(Long id) {
        return this.mediaRepository.getById(id);
    }

    public MediaComment getMediaCommentById(Long id) {
        return this.mediaCommentRepository.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public MediaFiles saveMedia(MediaFiles mediaFiles) {
        return this.mediaRepository.save(mediaFiles);
    }

    @Transactional(rollbackFor = Exception.class)
    public MediaComment saveMediaComment(MediaComment mediaComment) {
        return this.mediaCommentRepository.save(mediaComment);
    }

    public List<MediaComment> getMediaCommentsByMediaId(Long id) {
        return this.mediaCommentRepository.getMediaCommentsByMediaId(id);
    }
}
