package com.babydocs.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AWSS3Service {
    @Autowired
    private AmazonS3 s3Client;
    @Value("${aws.s3.bucket}")
    private String bucket;


    /* file upload using putObjectApi */
    public String uploadFile(final MultipartFile multipartFile, String path) {
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            return uploadFileToS3Bucket(bucket + "/" + path, file);

        } catch (final AmazonServiceException ex) {
            log.info("File upload is failed.");
            log.info(("Error= {} while uploading file." + ex.getMessage()));
        }
        return "n/a";
    }


    /* file upload using TransferMgr Api */
    public List<String> uploadFileList(MultipartFile[] multipartFile, String path) {
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();
        List<String> uploadedFiles = new ArrayList<>();
        List<PutObjectRequest> putObjectRequests = new ArrayList<>();

        Arrays.stream(multipartFile).forEach(mf -> {
            String dateTime = String.valueOf(LocalDateTime.now());
            String encodedDate = Base64.getEncoder().encodeToString(dateTime.getBytes());
            File file = convertMultiPartFileToFile(mf);
            final String uniqueFileName = encodedDate + "_" + file.getName();
            var request = new PutObjectRequest(bucket + "/" + path, uniqueFileName, file);
            putObjectRequests.add(request);
            uploadedFiles.add(uniqueFileName);
        });
        Upload upload = null;
        for (var putObjectRequest : putObjectRequests) {
            upload = tm.upload(putObjectRequest);
        }
        TransferMgr.waitForCompletion(Objects.requireNonNull(upload));
        return uploadedFiles;
    }

    public void deleteFile(final String keyName) {
        log.info("Deleting file with name= " + keyName);
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, keyName);
        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully.");
        } catch (AmazonServiceException e) {
            log.info("Amazing Service Exception::Error deleting object from S3" + e);
        } catch (Exception e) {
            log.info("Exception while deleting a project" + e);
        }
    }


    public void deleteFiles(String bucketName, List<String> keys) {

        String[] filesToDelete = keys.toArray(String[]::new);
        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucket)
                .withKeys(filesToDelete)
                .withQuiet(false);

        try {
            DeleteObjectsResult deleteObjectsResult = s3Client.deleteObjects(delObjReq);
            int successfulDeletes = deleteObjectsResult.getDeletedObjects().size();
            log.info(successfulDeletes + " objects successfully deleted.");
        } catch (AmazonServiceException e) {
            log.info("Amazing Service Exception::Error deleting object from S3" + e);
        } catch (Exception e) {
            log.info("Exception while deleting a project" + e);
        }

    }

    public List<String> uploadFileListNew(MultipartFile[] multipartFile,
                                          final String path) {

        List<File> files = new ArrayList<>();
        List<String> uploadedFiles = new ArrayList<>();
        for (MultipartFile mf : multipartFile) {
            File file = convertMultiPartFileToFile(mf);
            files.add(file);
            uploadedFiles.add(file.getName());
        }

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .withMultipartUploadThreshold((long) (5 * 1024 * 1024))
                .withExecutorFactory(() -> Executors.newFixedThreadPool(10))
                .build();

        MultipleFileUpload upload = tm.uploadFileList(bucket, path
                , new File("."), files);
        TransferMgr.waitForCompletion(upload);

        return uploadedFiles;

    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            System.out.println("Error converting the multi-part file to file= " + ex.getMessage());
        }
        return file;
    }

    private String uploadFileToS3Bucket(final String bucketName, final File file) {
        String dateTime = String.valueOf(LocalDateTime.now());
        String encodedDate = Base64.getEncoder().encodeToString(dateTime.getBytes());
        final String uniqueFileName = encodedDate + "_" + file.getName();
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        s3Client.putObject(putObjectRequest);
        return uniqueFileName;
    }
}
