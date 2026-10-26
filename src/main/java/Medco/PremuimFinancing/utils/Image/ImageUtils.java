package Medco.PremuimFinancing.utils.Image;

import org.apache.commons.io.FilenameUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
@Component
public class ImageUtils {

    public static byte[] compressImage(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[5*1024];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        while (!deflater.finished()) {
            int compressedDataLength = deflater.deflate(buffer);
            outputStream.write(buffer, 0, compressedDataLength);
        }

        outputStream.close();


        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[5*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }


        return outputStream.toByteArray();
    }

    public static String sanitizeImageName(String originalFilename) {

        String extension = FilenameUtils.getExtension(originalFilename);

        String sanitizedFilename = originalFilename
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_{2,}", "_")
                .trim();

        if (sanitizedFilename.length() > 240) {
            sanitizedFilename = sanitizedFilename.substring(0, 240);
        }

        String uniqueId = UUID.randomUUID().toString();


        String newFilename = String.format("%s_%s.%s",
                FilenameUtils.getBaseName(sanitizedFilename),
                uniqueId,
                extension);

        return newFilename.toLowerCase();

    }
    public static MultipartFile convertToMultipart(byte[] imageBytes, String fileName, String mimeType) {
        return new MockMultipartFile(
                "file",            // field name (can be anything)
                fileName,          // actual file name
                mimeType,          // content type
                imageBytes         // file content
        );
    }


//
//    public ResponseEntity<String> uploadBase64Image( Base64ImageRequest request) {
//        try {
//            // Decode base64 to byte[]
//            String base64Data = request.getBase64Image();
//
//            // Remove header if present (optional)
//            if (base64Data.contains(",")) {
//                base64Data = base64Data.split(",")[1];
//            }
//
//            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
//
//            // Create a MultipartFile
//            MultipartFile multipartFile = new MockMultipartFile(
//                    "file",
//                    request.getFileName(),
//                    request.getMimeType(),
//                    decodedBytes
//            );
//        }
//    }
}
