package Medco.PremuimFinancing.ServiceImpl.Institution;

import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Institution.InstitutionResponse;
import Medco.PremuimFinancing.Entity.Institution.Institution;
import Medco.PremuimFinancing.Repository.Institution.InstitutionRepository;
import Medco.PremuimFinancing.Service.Institution.InstitutionService;
import Medco.PremuimFinancing.exception.BadRequestException;
import Medco.PremuimFinancing.utils.Image.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionServiceImpl(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public Institution create(InstitutionCreateRequest request,MultipartFile logo) {

        return institutionRepository.save( mapCreateRequestToEntity(request, logo));
    }

    @Override
    public Institution update(UUID id, InstitutionUpdateRequest request,MultipartFile logo) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + id));
        mapUpdateRequestToEntity(request, institution,logo);
        return institutionRepository.save(institution);
    }

    @Override
    public void delete(UUID id) {
        if (!institutionRepository.existsById(id)) {
            throw new IllegalArgumentException("Institution not found with id: " + id);
        }
        institutionRepository.deleteById(id);
    }

    @Override
    public Institution get(UUID id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + id));
    }

    @Override
    public Page<Institution> list(Pageable pageable) {
        Page<Institution> institutions = institutionRepository.findAll(pageable);
        List<Institution> convertedList = institutions.getContent().stream()
                .peek(this::convertLogoToBase64)
                .collect(Collectors.toList());
        return new PageImpl<>(convertedList, pageable, institutions.getTotalElements());
    }

    /**
     * Maps create request data to institution entity
     *
     * @return
     */
    private Institution mapCreateRequestToEntity(InstitutionCreateRequest request, MultipartFile logo) {
        Institution institution = new Institution();
        institution.setName(request.getName());
        institution.setDescription(request.getDescription());
        institution.setWebsite(request.getWebsite());
        
        // Convert logo file to base64 if provided
        if (logo != null && !logo.isEmpty()) {
            try {
                byte[] base64Logo = logo.getBytes();
                if (base64Logo != null) {
                    institution.setLogo(ImageUtils.compressImage(base64Logo));
                }
                return institution;
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process logo file: " + e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw e; // Re-throw validation errors
            }
        }
        return institution;
    }

    /**
     * Maps update request data to institution entity
     */
    private void mapUpdateRequestToEntity(InstitutionUpdateRequest request, Institution institution,MultipartFile logo) {
        institution.setName(request.getName());
        institution.setDescription(request.getDescription());
        institution.setWebsite(request.getWebsite());

        // Only update logo if provided
        if (logo != null && !logo.isEmpty()) {
            try {
                byte[] base64Logo = logo.getBytes();
                if (base64Logo.length > 1048576)
                    throw new BadRequestException("Profile should be less than 1mb");
                if (base64Logo != null) {

                    institution.setLogo(ImageUtils.compressImage(base64Logo));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process logo file: " + e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw e; // Re-throw validation errors
            }
        }
    }

    /**
     * Converts MultipartFile to base64 string with data URI format
     */
    private String convertFileToBase64(MultipartFile file) throws IOException {
        // Validate file
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Validate file size (max 5MB)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
        }
        
        String contentType = file.getContentType();
        
        // Validate file type
        if (!isValidImageType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only PNG, JPEG, JPG, GIF, and WEBP are allowed");
        }
        
        String imageType = getImageType(contentType);
        byte[] fileBytes = file.getBytes();
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        return "data:image/" + imageType + ";base64," + base64String;
    }

    /**
     * Validates if the content type is a supported image type
     */
    private boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.contains("image/png") ||
               contentType.contains("image/jpeg") ||
               contentType.contains("image/jpg") ||
               contentType.contains("image/gif") ||
               contentType.contains("image/webp");
    }

    /**
     * Extracts image type from content type
     */
    private String getImageType(String contentType) {
        if (contentType == null) {
            return "png";
        }
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return "jpeg";
        } else if (contentType.contains("png")) {
            return "png";
        } else if (contentType.contains("gif")) {
            return "gif";
        } else if (contentType.contains("webp")) {
            return "webp";
        }
        return "png"; // default
    }

    /**
     * Converts logo byte array to base64 data URI string
     */
    private void convertLogoToBase64(Institution institution) {
        if (institution.getLogo() != null && institution.getLogo().length > 0) {
            String base64String = Base64.getEncoder().encodeToString(institution.getLogo());
            // Assume PNG as default, you can enhance this by storing the image type
            institution.setLogoBase64("data:image/png;base64," + base64String);
        }
    }
}
