package Medco.PremuimFinancing.Service.Institution;

import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Institution.InstitutionUpdateRequest;
import Medco.PremuimFinancing.Entity.Institution.Institution;
import jakarta.mail.Multipart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface InstitutionService {
    Institution create(InstitutionCreateRequest request, MultipartFile logo);
    Institution update(UUID id, InstitutionUpdateRequest request,MultipartFile logo);
    void delete(UUID id);
    Institution get(UUID id);
    Page<Institution> list(Pageable pageable);
}
