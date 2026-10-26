package Medco.PremuimFinancing.bootstrap;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Enums.User.Status;
import Medco.PremuimFinancing.Enums.User.UserType;
import Medco.PremuimFinancing.Repository.Role.PrivilegeRepository;
import Medco.PremuimFinancing.Repository.Role.RoleRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SebnaSeeder implements CommandLineRunner {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SebnaSeeder(PrivilegeRepository privilegeRepository, RoleRepository roleRepository, 
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // create privileges (idempotent)
        Map<String, Privilege> p = new HashMap<>();

        // global scope
        addPriv(p, "create_privilege", "privilege", "global", "Create privilege");
        addPriv(p, "read_privilege", "privilege", "global", "Read privilege");
        addPriv(p, "read_privileges", "privilege", "global", "Read privileges");
        addPriv(p, "update_privilege", "privilege", "global", "Update privilege");
        addPriv(p, "delete_privilege", "privilege", "global", "Delete privilege");

        addPriv(p, "create_role", "role", "global", "Create role");
        addPriv(p, "read_role", "role", "global", "Read role");
        addPriv(p, "read_roles", "role", "global", "Read roles");
        addPriv(p, "update_role", "role", "global", "Update role");
        addPriv(p, "delete_role", "role", "global", "Delete role");

        addPriv(p, "read_logs", "security", "global", "Read login logs");
        addPriv(p, "read_reports", "report", "global", "Read dashboard reports");

        // institution scope
        addPriv(p, "create_user", "user", "global", "Create user");
        addPriv(p, "read_user", "user", "global", "Read user");
        addPriv(p, "read_users", "user", "global", "Read users");
        addPriv(p, "update_user", "user", "global", "Update user");
        addPriv(p, "delete_user", "user", "global", "Delete user");

        addPriv(p, "create_post", "post", "global", "Create post");
        addPriv(p, "read_post", "post", "global", "Read post");
        addPriv(p, "read_posts", "post", "global", "Read posts");
        addPriv(p, "update_post", "post", "global", "Update post");
        addPriv(p, "delete_post", "post", "global", "Delete post");

        addPriv(p, "create_comment", "comment", "global", "Create comment");
        addPriv(p, "read_comments", "comment", "global", "Read comments");
        addPriv(p, "delete_comment", "comment", "global", "Delete comment");

        addPriv(p, "like_post", "like", "global", "Like post");
        addPriv(p, "unlike_post", "like", "global", "Unlike post");

        addPriv(p, "send_message", "message", "global", "Send message");
        addPriv(p, "read_messages", "message", "global", "Read messages");

        // persist any that don't exist
        for (Privilege priv : p.values()) {
            if (!privilegeRepository.existsByPrivilegeName(priv.getPrivilegeName())) {
                privilegeRepository.save(priv);
            }
        }

        // seed roles and attach privileges
        seedRole("super_admin", "Platform super admin", Set.of(
                "create_privilege","read_privilege","read_privileges","update_privilege","delete_privilege",
                "create_role","read_role","read_roles","update_role","delete_role",
                "read_logs",
                "read_reports",
                "create_user","read_user","read_users","update_user","delete_user",
                "create_post","read_post","read_posts","update_post","delete_post",
                "create_comment","read_comments","delete_comment",
                "like_post","unlike_post",
                "send_message","read_messages"
        ));

        seedRole("shareholders", "Shareholder user", Set.of(
                "read_posts","read_post",
                "create_comment","read_comments",
                "like_post","unlike_post",
                "send_message","read_messages"
        ));

        // Create superadmin user if it doesn't exist
        seedSuperAdminUser();
    }

    private void seedRole(String name, String desc, Set<String> privNames) {
        Optional<Role> existing = roleRepository.findByRoleName(name);
        Role role = existing.orElseGet(() -> {
            Role r = new Role();
            r.setRoleName(name);
            r.setRoleDescription(desc);
            return r;
        });
        Set<Privilege> privs = new HashSet<>();
        for (String n : privNames) {
            Privilege pr = privilegeRepository.findByPrivilegeName(n);
            if (pr != null) privs.add(pr);
        }
        role.setPrivileges(privs);
        roleRepository.save(role);
    }

    private void addPriv(Map<String, Privilege> map, String name, String category, String scope, String desc) {
        Privilege pr = new Privilege(name, desc, category);
        pr.setScope(scope);
        map.put(name, pr);
    }

    private void seedSuperAdminUser() {
        Optional<Role> superAdminRole = roleRepository.findByRoleName("super_admin");
        if (superAdminRole.isEmpty()) {
            return;
        }

        UserEntity existing = userRepository.findByEmail("admin@gmail.com");
        if (existing != null) {
            if (existing.getPasswordResetCode() != null && !existing.getPasswordResetCode().isBlank()) {
                existing.setPassword(passwordEncoder.encode("password"));
                existing.setPasswordResetCode(null);
                existing.setEmailVerificationToken(null);
                existing.setUserStatus(Status.ACTIVE);
                existing.setDeleted(false);
                if (existing.getRole() == null) {
                    existing.setRole(superAdminRole.get());
                }
                userRepository.save(existing);
            }
            return;
        }

        UserEntity existingByPhone = userRepository.findByMobilePhone("+251900000000");
        if (existingByPhone != null) {
            return;
        }

        UserEntity superAdmin = new UserEntity();
        superAdmin.setEmail("admin@gmail.com");
        superAdmin.setPassword(passwordEncoder.encode("password"));
        superAdmin.setFirstName("Super");
        superAdmin.setFatherName("Admin");
        superAdmin.setGrandFatherName("System");
        superAdmin.setMobilePhone("+251900000000");
        superAdmin.setGender("Male");
        superAdmin.setUserStatus(Status.ACTIVE);
        superAdmin.setRole(superAdminRole.get());
        superAdmin.setUserUuid(UUID.randomUUID().toString());
        userRepository.save(superAdmin);
    }
}

