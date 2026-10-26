package Medco.PremuimFinancing.Repository.Role;


import Medco.PremuimFinancing.Entity.UserRole.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByRoleName(String roleName);
    Boolean existsByRoleDescription(String roleDescription);
    Role findByRoleUuid(String roleUuid);
    Page<Role> findByRoleNameContainingIgnoreCaseOrRoleDescriptionContainingIgnoreCase(String searchTerm1, String searchTerm2, Pageable pageRequest);
    List<Role> findAllByRoleDescription(String roleDescription);
    List<Role> findAllByRoleName(String roleName);
    Optional<Role> findByRoleName(String name);

    Page<Role> findByRoleNameContainingIgnoreCase(String search, Pageable pageable);
}
