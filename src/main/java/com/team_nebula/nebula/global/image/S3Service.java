package com.team_nebula.nebula.global.image;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.util.HashUtil;
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
    private static final String FAVICON_FILE_DIR = "favicons/";


    public String saveThumbnail(MultipartFile thumbnailImage, String dataInfo) {
        return uploadThumbnailToS3(thumbnailImage, THUMBNAIL_DIR, dataInfo);
    }

    public String saveHtmlFile(MultipartFile htmlFile, String dataInfo, Long userId) {
        return uploadHtmlToS3(htmlFile, HTML_FILE_DIR, dataInfo, userId);
    }

    public String saveFavicon(File faviconFile, String domain) {
        String fileName = FAVICON_FILE_DIR + domain + ".png";
        return uploadFileToS3(faviconFile, fileName);
    }

    private String uploadHtmlToS3(MultipartFile file, String dirName, String dataInfo, Long userId)  {
        File uploadFile = convert(file)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MULTIPARTFILE_CONVERT_FAIL));
        String hashUserId = HashUtil.hashUserId(userId);

        String fileName = dirName + hashUserId + "/" + dataInfo + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

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

    private String uploadFileToS3(File file, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void putHtmlS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
    }

    private String putThumbnailS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String putFaviconS3(String faviconPath, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, faviconPath));

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

    public String sanitizeTitleForS3(String title) {
        if (title == null) return "";

        // 1. 공백을 하나로 정규화
        title = title.replaceAll("\\s+", " ");

        // 2. 한글, 영문, 숫자, '-', '_', '.', '~', 공백만 허용 (이모티콘, 특수문자, 대괄호 등 제거)
        title = title.replaceAll("[^가-힣a-zA-Z0-9\\-_.~ ]", "");

        // 3. 공백을 하이픈으로 변환
        title = title.replace(" ", "-");

        // 4. 소문자로 변환
        title = title.toLowerCase();

        return title;
    }
}

