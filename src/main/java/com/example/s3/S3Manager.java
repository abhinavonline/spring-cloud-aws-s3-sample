package com.example.s3;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@Component
public class S3Manager {

    private static final Logger LOG = LoggerFactory.getLogger(S3Manager.class);
    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.upload.dir}")
    private String uploadFilePath;

    @Value("${aws.s3.bucket}")
    private String s3bucket;

    @Value("${aws.s3.upload.key}")
    private String s3UploadKey;

    @Value("${aws.s3.download.key}")
    private String s3DownloadKey;

    @Value("${aws.s3.download.dir}")
    private String downloadDir;


    public int uploadFilesToS3() throws IOException {
        //File types based on extensions and uploading from subfolders within uploadFilePath can be managed here.
        Collection<File> files = FileUtils.listFiles(new File(uploadFilePath), null, false);
        LOG.info("Starting to Upload {} PIM File(s) to S3", files.size());

        for (File file : files) {
            s3Client.uploadObjecttoS3(s3bucket, s3UploadKey, file);
            File destDir = new File(uploadFilePath + "arc");
            LOG.info("Moving file {} to arc dir", file.getName());
            FileUtils.copyFileToDirectory(file, destDir);
            Files.delete(Paths.get(file.getPath()));

        }
        return files.size();
    }

    public int downloadFilesFromS3() throws InterruptedException {
        LOG.info("Starting to Download  file(s) from S3 bucket {}, key {}", s3bucket, s3DownloadKey);
        return s3Client.downloadObjectsFromS3(s3bucket, s3DownloadKey, downloadDir);

    }

}



