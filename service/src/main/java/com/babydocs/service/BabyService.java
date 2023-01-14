package com.babydocs.service;

import com.babydocs.model.Baby;
import com.babydocs.repository.BabyRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class BabyService {
    private final BabyRepository babyRepository;

    @Transactional(rollbackFor = Exception.class)
    public Baby saveBaby(Baby baby) {
        return this.babyRepository.save(baby);
    }

    public Optional<List<Baby>> findBabyByUserName(String username) {
        return babyRepository.findBabyByUsername(username);
    }

    public Optional<Baby> findBabyById(Long id) {
        return babyRepository.findById(id);
    }

}
