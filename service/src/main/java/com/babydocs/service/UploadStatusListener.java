package com.babydocs.service;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;


@Slf4j
public class UploadStatusListener implements ProgressListener {
    final PutObjectRequest putObjectRequest;
    final CountDownLatch signal;

    public UploadStatusListener(PutObjectRequest objectRequest, CountDownLatch signal) {
        this.putObjectRequest = objectRequest;
        this.signal = signal;
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        if (progressEvent.getEventType() == ProgressEventType.TRANSFER_STARTED_EVENT) {
            log.info("Upload Started:" + this.putObjectRequest.getFile().getName() + " " + Thread.currentThread().getName());
        }
        if (progressEvent.getEventType() == ProgressEventType.TRANSFER_COMPLETED_EVENT) {
            log.info("Upload Completed:" + this.putObjectRequest.getFile().getName() + " " + Thread.currentThread().getName());
            this.signal.countDown();
        }
        if (progressEvent.getEventType() == ProgressEventType.TRANSFER_FAILED_EVENT) {
            log.info("Upload Failed:" + this.putObjectRequest.getFile().getName() + " " + Thread.currentThread().getName());
            this.signal.countDown();
        }
        if (progressEvent.getEventType() == ProgressEventType.TRANSFER_CANCELED_EVENT) {
            log.info("Upload Canceled:" + this.putObjectRequest.getFile().getName() + " " + Thread.currentThread().getName());
            this.signal.countDown();
        }
    }
}
