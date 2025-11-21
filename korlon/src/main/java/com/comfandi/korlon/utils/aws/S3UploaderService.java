package com.comfandi.korlon.utils.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class S3UploaderService {

    private final S3Client s3Client;

    private String bucketName;


    public S3UploaderService(@Value("${aws.region}") String region,
                             @Value("${aws.access-key}") String accessKey,
                             @Value("${aws.secret-key}") String secretKey,
                             @Value("${aws.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
    	
    	AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey); // USER AND PASS
    	StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCreds);
    	
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider) //DefaultCredentialsProvider.create()
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String key = "uploads/" + UUID.randomUUID() + extension;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, URLEncoder.encode(key, StandardCharsets.UTF_8));
    }
    
    public ResponseInputStream<GetObjectResponse> downloadFileAsStream(String key) {
        S3Client s3Client = this.s3Client;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
    
    public String uploadFileFromDisk(File file, String keyPrefix) {
        String fileName = file.getName();
        String key = keyPrefix + "/" + UUID.randomUUID() + "_" + fileName;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));
        return key;
        //return String.format("https://%s.s3.amazonaws.com/%s", bucketName, URLEncoder.encode(key, StandardCharsets.UTF_8));
    }
    
    public String uploadPdfFile(File file, String keyPrefix) throws FileNotFoundException, IOException {
        String fileName = file.getName();
        String key = keyPrefix + "-" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        try (InputStream inputStream = new FileInputStream(file)) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.length()));
        }
        return key;
        //return String.format("https://%s.s3.amazonaws.com/%s", bucketName, URLEncoder.encode(key, StandardCharsets.UTF_8));
    }

}
