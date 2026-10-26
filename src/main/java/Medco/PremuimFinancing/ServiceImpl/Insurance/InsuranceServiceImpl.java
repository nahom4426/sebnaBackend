package Medco.PremuimFinancing.ServiceImpl.Insurance;


import Medco.PremuimFinancing.Dto.Response.InsuranceResponse;
import Medco.PremuimFinancing.Entity.Insurance.Insurance;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Insurance.InsuranceRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Insurance.InsuranceService;
import Medco.PremuimFinancing.ServiceImpl.User.UserDetailsImpl;
import Medco.PremuimFinancing.exception.BadRequestException;
import Medco.PremuimFinancing.utils.Image.ImageUtils;
import Medco.PremuimFinancing.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InsuranceServiceImpl implements InsuranceService {
    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;
//    public  ImageUtils imageUtils;

    public InsuranceServiceImpl(InsuranceRepository insuranceRepository, UserRepository userRepository) {
        this.insuranceRepository = insuranceRepository;

        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> registerInsurance(String insuranceName, String accountNumber, MultipartFile image) throws IOException {
        UserDetailsImpl userDetails = SecurityUtils.getAuthenticatedUser();
        Insurance insurance=new Insurance();

        insurance.setInsuranceName(insuranceName);
        insurance.setAccountNumber(accountNumber);
        byte[] profilePictureBytes = image.getBytes();

        if (profilePictureBytes.length > 1048576)
            throw new BadRequestException("Profile should be less than 1mb");

        insurance.setProfile(ImageUtils.compressImage(profilePictureBytes));
        insurance.setRegisteredBy(userDetails.getUserUuid());
        insurance.setRegistrationDate(new Date());
        Insurance savedInsurance=insuranceRepository.save(insurance);

        return ResponseEntity.ok(getInsuranceResponse(savedInsurance));
    }

    @Override
    public ResponseEntity<?> getAll(String search, Pageable pageable) {
        Page <Insurance> insurancePage=search!=null?insuranceRepository.findByInsuranceName(search,pageable):insuranceRepository.findAll(pageable);
        List<InsuranceResponse>responseList=insurancePage.stream().map(this::getInsuranceResponse).toList();

        return ResponseEntity.ok(responseList);

    }

    private InsuranceResponse getInsuranceResponse(Insurance insurance) {
        UserDetailsImpl userDetails = SecurityUtils.getAuthenticatedUser();
        InsuranceResponse insuranceResponse=new InsuranceResponse();
        BeanUtils.copyProperties(insurance,insuranceResponse);
        Optional <UserEntity> user=userRepository.findByUserUuid(insurance.getRegisteredBy());
        user.ifPresent(userEntity -> insuranceResponse.setRegisteredBy(userEntity.getFirstName()+" "+userEntity.getFatherName()));
        insuranceResponse.setRegistrationDate(insurance.getRegistrationDate());
        insuranceResponse.setUpdatedDate(insurance.getUpdatedDate());
        Optional <UserEntity> updater=userRepository.findByUserUuid(insurance.getUpdatedBy());
        user.ifPresent(userEntity -> insuranceResponse.setRegisteredBy(userEntity.getFirstName()+" "+userEntity.getFatherName()));
        byte[] decompressedData;

        if (insurance.getProfile()==null) {
            decompressedData = null;

        }
        else {
            decompressedData = ImageUtils.decompressImage(insurance.getProfile());

        }
        MultipartFile image=decompressedData!=null?ImageUtils.convertToMultipart(decompressedData,"profile","image/jpeg"):null;
//            insuranceResponse.setImage(image);
        insuranceResponse.setProfile(decompressedData);

//            if (decompressedData!=null)
//             insuranceResponse.setImage(Base64.getEncoder().encodeToString(decompressedData));
        return insuranceResponse;
    }

    @Override
    public ResponseEntity<?> editInsurance(String insuranceUuid, String insuranceName, String accountNumber, MultipartFile image) throws IOException {
        UserDetailsImpl userDetails = SecurityUtils.getAuthenticatedUser();
        Insurance insurance=insuranceRepository.findByInsuranceUuid(insuranceUuid).orElseThrow(()->new BadRequestException("insurance not found"));

        insurance.setInsuranceName(insuranceName);
        insurance.setAccountNumber(accountNumber);
        byte[] profilePictureBytes = image.getBytes();

        if (profilePictureBytes.length > 1048576)
            throw new BadRequestException("Profile should be less than 1mb");

        insurance.setProfile(ImageUtils.compressImage(profilePictureBytes));
        insurance.setUpdatedBy(userDetails.getUserUuid());
        insurance.setUpdatedDate(new Date());
        Insurance savedInsurance=insuranceRepository.save(insurance);

        return ResponseEntity.ok(getInsuranceResponse(savedInsurance));
    }

    @Override
    public ResponseEntity<?> getInsurance(String insuranceUUid) {
        Insurance insurance =insuranceRepository.findByInsuranceUuid(insuranceUUid).orElseThrow(()->new BadRequestException("insurance not found"));
        return ResponseEntity.ok(getInsuranceResponse(insurance));

    }

}
