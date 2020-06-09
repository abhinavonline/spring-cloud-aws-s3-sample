package com.example.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Component
public class S3Client {

    private static final Logger LOG = LoggerFactory.getLogger(S3Client.class);
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    ResourcePatternResolver resourcePatternResolver;
    @Autowired
    AmazonS3 amazonS3;

    public void uploadObjecttoS3(String s3bucket, String s3key, File file) throws IOException {

        byte[] fileBytes = FileUtils.readFileToByteArray(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileBytes.length);
        //Enable server-side encryption in your object metadata when you upload your data to Amazon S3
        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        PutObjectRequest putRequest = new PutObjectRequest(s3bucket,
                s3key + file.getName(),
                new ByteArrayInputStream(fileBytes),
                objectMetadata);
        amazonS3.putObject(putRequest);
        LOG.info("Object  {}{} uploaded to {}", s3key, file.getName(), s3bucket);
    }


    public int downloadObjectsFromS3(String s3bucket, String s3key,
                                     String dirPath) throws InterruptedException {
        ObjectListing ol = amazonS3.listObjects(s3bucket, s3key);
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(this.amazonS3).build();
        LOG.info("Downloading to host directory: {} ", dirPath);
        int count = 0;
        for (S3ObjectSummary objSummary : ol.getObjectSummaries()) {
            String downloadKey = objSummary.getKey();
            if (!downloadKey.endsWith("/")) {
                Download transfer = transferManager.download(s3bucket, downloadKey, new File(dirPath + downloadKey));
                while (!transfer.isDone()) {
                    Thread.sleep(2000);
                    LOG.info("Transfer: {}", transfer.getDescription());
                    LOG.info("  - State: {}", transfer.getState());
                    LOG.info("  - Percent Complete: {}",
                            transfer.getProgress().getPercentTransferred());
                }
                transfer.waitForCompletion();
                LOG.info("Deleting key {} from S3 bucket {}  ", downloadKey, s3bucket);
                amazonS3.deleteObject(s3bucket, downloadKey);
                ++count;
            }
        }
        transferManager.shutdownNow();
        LOG.info("{} File(s) downloaded ", count);
        return count;
    }

}