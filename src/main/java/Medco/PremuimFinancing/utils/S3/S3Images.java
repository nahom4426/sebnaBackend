package Medco.PremuimFinancing.utils.S3;

import Medco.PremuimFinancing.exception.BadRequestException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class S3Images {

    @Value("${spring.file.upload_libre}")
    private static String uploadDirectory;
    @Value("${aws.s3.bucket}")
    private static String bucketName;
    @Value("${aws.s3.access-key}")
    private static String accessKey;

    @Value("${aws.s3.secret-key}")
    private static String secretKey;

    @Value("${aws.s3.region}")
    private static String region;

//    private static AmazonS3 s3Client;
//
//    private static AmazonS3 getS3Client() {
//        if (s3Client == null) {
//            s3Client = AmazonS3ClientBuilder.standard()
//                    .withRegion(region)  // Dynamic region
//                    .withCredentials(new AWSStaticCredentialsProvider(
//                            new BasicAWSCredentials(accessKey, secretKey)))
//                    .build();
//        }
//        return s3Client;
//    }
private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.ME_SOUTH_1) // Middle East (Bahrain)
        .build();
    public static String convertToBas64(S3Object s3Object) {

        try (InputStream objectData = s3Object.getObjectContent()) {

            byte[] bytes = IOUtils.toByteArray(objectData);

            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read S3 object content", e);
        } finally {

            try {
                s3Object.close();
            } catch (IOException e) {

                System.err.println("Failed to close S3Object: " + e.getMessage());
            }
        }
    }
    public static void saveToS3(String fileName, MultipartFile file) throws IOException {
        String key = uploadDirectory + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        validateFileType(file.getContentType());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        PutObjectResult putObjectResponse = s3Client.putObject(putObjectRequest);

    }
    public static void validateFileType(String contentType) {
        List<String> supportedTypes = Arrays.asList("application/pdf", "image/jpeg", "image/png");
        if (!supportedTypes.contains(contentType)) {
            throw new BadRequestException("Unsupported file type: " + contentType);
        }
    }
}
