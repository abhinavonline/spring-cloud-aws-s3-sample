package com.example.application;

import com.example.s3.S3Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class S3FileExchangeController {
    private static final Logger LOG = LoggerFactory.getLogger(S3FileExchangeController.class);

    @Autowired
    private S3Manager s3Manager;

    @GetMapping(path = "/upload")
    public ResponseEntity<String> uploadFilesToS3() throws IOException {
        int filesCount = s3Manager.uploadFilesToS3();
        return ResponseEntity.ok(filesCount + " Files were uploaded to AWS S3!");
    }

    @GetMapping(path = "/download")
    public ResponseEntity<String> downloadFilesFromS3() throws InterruptedException {
        int filesCount = s3Manager.downloadFilesFromS3();
        return ResponseEntity.ok(filesCount + " File(s) were downloaded from AWS S3!");

    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleAll(final Exception ex) {
        LOG.info(ex.getMessage());
        return new ResponseEntity<>(ex.getLocalizedMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
