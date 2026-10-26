package Medco.PremuimFinancing.Repository.Role;


import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByPrivilegeName(String privilegeName);
//    @Query("SELECT p.privilegeName FROM Privilege p INNER JOIN RolePrivilege rp ON p.id = rp.id WHERE rp.id = :roleUuid ORDER BY p.privilegeCategory ASC")
//    List<String> findPrivilegeNamesByRoleUuid(@Param("roleUuid") String roleUuid);


//    @Query("SELECT p.privilegeName, p.privilegeCategory FROM Privilege p INNER JOIN RolePrivilege rp ON p.id = rp.id WHERE rp.id = :roleUuid Order by p.privilegeCategory ASC")
//    List<String> findPrivilegeNamesAndCategoryByRoleUuid(@Param("roleUuid") Long roleUuid);

    boolean existsByPrivilegeName(String privilegeName);

//    boolean existsByPrivilegeDescription(String privilegeDescription);

    Privilege findByPrivilegeUuid(String privilegeString);

    List<Privilege> findAllByPrivilegeName(String privilegeName);
    List<Privilege> findAllByPrivilegeDescription(String privilegeDescription);
    Page<Privilege> findByPrivilegeNameContainingIgnoreCaseOrPrivilegeDescriptionContainingIgnoreCaseOrPrivilegeCategoryContainingIgnoreCase(String searchTerm1, String searchTerm2, String searchTerm3,
                                                                                                               Pageable pageRequest);


    Set<Privilege> findByPrivilegeUuidIn(Set<String> privilegeUuid);
}
