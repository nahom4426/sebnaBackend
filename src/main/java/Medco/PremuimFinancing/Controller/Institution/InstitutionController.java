package Medco.PremuimFinancing.Controller.Institution;

import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionUpdateRequest;
import Medco.PremuimFinancing.Entity.Institution.Institution;
import Medco.PremuimFinancing.Service.Institution.InstitutionService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST Controller for Institution management
 * Handles CRUD operations for institutions with role-based access control
 */
@RestController
@RequestMapping("/api/sebna/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    /**
     * Create a new institution
     * @param name Institution name (required)
     * @param description Institution description (optional)
     * @param website Institution website (optional)
     * @param logo Institution logo file (optional)
     * @return Created institution entity
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_create_institution')")
    public ResponseEntity<Institution> create(
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Institution name is required");
        }

        // Validate name length
        if (name.trim().length() < 2 || name.trim().length() > 150) {
            throw new IllegalArgumentException("Institution name must be between 2 and 150 characters");
        }

        InstitutionCreateRequest request = new InstitutionCreateRequest();
        request.setName(name.trim());
        request.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        request.setWebsite(website != null && !website.trim().isEmpty() ? website.trim() : null);
//        request.setLogo(logo);

        Institution created = institutionService.create(request,logo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing institution
     * @param id Institution ID
     * @param name Institution name (required)
     * @param description Institution description (optional)
     * @param website Institution website (optional)
     * @param logo Institution logo file (optional)
     * @return Updated institution entity
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_update_institution')")
    public ResponseEntity<Institution> update(
            @PathVariable UUID id,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {
        
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Institution name is required");
        }
        
        // Validate name length
        if (name.trim().length() < 2 || name.trim().length() > 150) {
            throw new IllegalArgumentException("Institution name must be between 2 and 150 characters");
        }
        
        InstitutionUpdateRequest request = new InstitutionUpdateRequest();
        request.setName(name.trim());
        request.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        request.setWebsite(website != null && !website.trim().isEmpty() ? website.trim() : null);
//        request.setLogo(logo);
        
        Institution updated = institutionService.update(id, request, logo);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete an institution by ID
     * @param id Institution ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_delete_institution')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        institutionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get institution by ID
     * @param id Institution ID
     * @return Institution entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<Institution> get(@PathVariable UUID id) {
        Institution institution = institutionService.get(id);
        return ResponseEntity.ok(institution);
    }

    /**
     * Get all institutions with pagination
     * @param page Page number (default: 1)
     * @param limit Number of items per page (default: 25)
     * @return Paginated list of institutions
     */
    @GetMapping
    public ResponseEntity<Page<Institution>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "id", "desc");
        Page<Institution> institutions = institutionService.list(pageable);
        return ResponseEntity.ok(institutions);
    }
}
