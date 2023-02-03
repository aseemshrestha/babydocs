package com.babydocs.service;

import com.babydocs.model.Activity;
import com.babydocs.repository.ActivityRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Data
public class ActivityService {
    private final ActivityRepository activityRepository;

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveActivity(Activity activity) {
        this.activityRepository.save(activity);
    }

}
