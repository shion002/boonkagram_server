package findcafe.cafe.service;

import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;

    @Value("${cloud.aws.s3.region}")
    private String region;

    public PresignedUrlResponse generatePresignedUrl(String fileName, String fileType, String s3Url) {
        String uniqueFileName = generateUniqueFileName(fileName);
        String s3Key = "cafe/" + s3Url + "/" + uniqueFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(fileType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, s3Key);

        return new PresignedUrlResponse(presignedUrl, fileUrl);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }

    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                log.warn("fileUrl이 null 또는 빈 문자열입니다.");
                return;
            }


            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            log.info("🔍 원본 URL: {}", fileUrl);

            URL url = new URL(fileUrl);
            String path = url.getPath();

            String key = path.startsWith("/") ? path.substring(1) : path;

            return key;

        } catch (Exception e) {
            throw new RuntimeException("Key 추출 실패", e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(folderPath + "/")
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            for(S3Object s3Object : listResponse.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Object.key())
                        .build());
            }
            log.info("S3 폴더 삭제: {}", folderPath);
        } catch (Exception e) {
            log.error("S3 폴더 삭제 실패: {}", folderPath, e);
        }
    }

}
