package com.team_nebula.nebula.domain.image;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String THUMBNAIL_DIR = "thumbnails/";
    private static final String HTML_FILE_DIR = "html_files/";


    public String saveThumbnail(MultipartFile thumbnailImage, String dataInfo) {
        return uploadThumbnailToS3(thumbnailImage, THUMBNAIL_DIR, dataInfo);
    }

    public String saveHtmlFile(MultipartFile htmlFile, String dataInfo) {
        return uploadHtmlToS3(htmlFile, HTML_FILE_DIR, dataInfo);
    }

    private String uploadHtmlToS3(MultipartFile file, String dirName, String dataInfo)  {
        File uploadFile = convert(file)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MULTIPARTFILE_CONVERT_FAIL));

        String fileName = dirName + dataInfo + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        putHtmlS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return fileName;
    }

    private String uploadThumbnailToS3(MultipartFile file, String dirName, String dataInfo)  {
        File uploadFile = convert(file)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MULTIPARTFILE_CONVERT_FAIL));

        String fileName = dirName + dataInfo + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        String fileUrl = putThumbnailS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return fileUrl;
    }

    private void putHtmlS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
    }

    private String putThumbnailS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.warn("File delete fail");
        }
    }

    private Optional<File> convert(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.error("File is empty or filename is null");
            return Optional.empty();
        }

        try {
            File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename());
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            } else {
                log.error("File already exists: {}", convertFile.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("File conversion failed: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void deleteHtmlFileInS3(String fileKey){
        try{
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
            log.info("Deleted HTML file from S3: {}", fileKey);
        }
        catch(AmazonServiceException e){
            log.error("Failed to delete HTML file from S3: {}", fileKey, e);
            throw new GeneralException(ErrorStatus._S3_HTML_FILE_DELETE_FAIL);
        }
    }
}

