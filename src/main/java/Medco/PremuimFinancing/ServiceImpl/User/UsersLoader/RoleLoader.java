package Medco.PremuimFinancing.ServiceImpl.User.UsersLoader;

import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Repository.Role.PrivilegeRepository;
import Medco.PremuimFinancing.Repository.Role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RoleLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeLoader privilegeLoader;

    public RoleLoader(RoleRepository roleRepository, PrivilegeRepository privilegeRepository, PrivilegeLoader privilegeLoader) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.privilegeLoader = privilegeLoader;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role role = new Role();
            role.setRoleName("admin");
            role.setRoleDescription("controls the system");


            List<String> privilegeNames = List.of(
                    "create_role", "read_role", "update_role", "delete_role",
                     "read_privilege","read_privileges",  "delete_privilege",
                    "create_user","read_user","read_users","delete_user"
            );


            Set<Privilege> privileges = new HashSet<>();
            for (String name : privilegeNames) {
                if (privilegeRepository.count() == 0) {
                    privilegeLoader.run();
                }

                Privilege privilege = privilegeRepository.findByPrivilegeName(name);
                if (privilege != null) {
                    privileges.add(privilege);
                }
            }


            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
}
