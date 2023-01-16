package com.babydocs.service;

import com.babydocs.repository.MediaRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Data
public class MediaService {
    private final MediaRepository mediaRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageById(Long id) {
        this.mediaRepository.deleteByMediaId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageByMediaLocation(String mediaLocation) {
        this.mediaRepository.deleteByMediaLocation(mediaLocation);
    }
}
