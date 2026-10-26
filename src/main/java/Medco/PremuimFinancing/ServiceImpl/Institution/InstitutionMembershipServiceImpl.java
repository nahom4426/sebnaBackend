package Medco.PremuimFinancing.ServiceImpl.Institution;

import Medco.PremuimFinancing.Entity.Institution.Institution;
import Medco.PremuimFinancing.Entity.Institution.UserInstitution;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Institution.InstitutionRepository;
import Medco.PremuimFinancing.Repository.Institution.UserInstitutionRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Institution.InstitutionMembershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InstitutionMembershipServiceImpl implements InstitutionMembershipService {

    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final UserInstitutionRepository userInstitutionRepository;

    public InstitutionMembershipServiceImpl(InstitutionRepository institutionRepository, UserRepository userRepository, UserInstitutionRepository userInstitutionRepository) {
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.userInstitutionRepository = userInstitutionRepository;
    }

    @Override
    @Transactional
    public void assignUser(UUID institutionId, String userUuid) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow();
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        UserInstitution ui = userInstitutionRepository.findByUser(user).orElse(new UserInstitution());
        ui.setUser(user);
        ui.setInstitution(institution);
        userInstitutionRepository.save(ui);
    }

    @Override
    @Transactional
    public void unassignUser(UUID institutionId, String userUuid) {
        Institution institution = institutionRepository.findById(institutionId).orElseThrow();
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        userInstitutionRepository.findByUser(user).ifPresent(ui -> {
            if (ui.getInstitution().getId().equals(institution.getId())) {
                userInstitutionRepository.delete(ui);
            }
        });
    }
}
