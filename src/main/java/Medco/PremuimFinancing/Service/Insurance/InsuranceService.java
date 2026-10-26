package Medco.PremuimFinancing.Service.Insurance;

import Medco.PremuimFinancing.Dto.Request.Insurance.InsuranceRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface InsuranceService {

    ResponseEntity<?> registerInsurance(String insuranceName, String accountNumber, MultipartFile image) throws IOException;

    ResponseEntity<?> getAll(String search, Pageable pageable);

    ResponseEntity<?> editInsurance(String insuranceUuid, String insuranceName, String accountNumber, MultipartFile image) throws IOException;

    ResponseEntity<?> getInsurance(String insuranceUUid);
}
