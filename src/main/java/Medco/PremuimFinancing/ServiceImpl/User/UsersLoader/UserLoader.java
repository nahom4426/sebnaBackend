package Medco.PremuimFinancing.ServiceImpl.User.UsersLoader;


import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Repository.Role.RoleRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.exception.BadRequestException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserLoader implements CommandLineRunner {
    private  final UserRepository userRepository;
    private  final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleLoader roleLoader;

    public UserLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RoleLoader roleLoader) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleLoader = roleLoader;
    }


    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {

            UserEntity admin = new UserEntity();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(  passwordEncoder.encode("passme"));

            admin.setFirstName("admin ");
            admin.setFatherName("Default");
            admin.setGrandFatherName("User");
            admin.setBirthDate(new Date());
            admin.setGender("Male");
            admin.setMobilePhone("0900000000");


            if (roleRepository.count() == 0)
                roleLoader.run();
            Role role= roleRepository.findByRoleName("super_admin")
                    .orElseThrow(() -> new BadRequestException("Role Not find with the provided Name"));


            admin.setRole(role);


            userRepository.save(admin);
        }

}
    }