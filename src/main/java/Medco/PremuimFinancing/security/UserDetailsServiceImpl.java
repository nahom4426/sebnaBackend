package Medco.PremuimFinancing.security;


import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.ServiceImpl.User.UserDetailsImpl;
import Medco.PremuimFinancing.exception.EmailAlreadyExists;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

   private final UserRepository userRepository;


    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        UserEntity user = userRepository.findByEmailOrMobilePhone(email,email);
        if (user==null) throw new EmailAlreadyExists("User Not Found with user name: " + email);

        List<String> privilegesForRole = user.getRole().getPrivileges().stream().map(privilege -> "ROLE_" + privilege.getPrivilegeName()).toList();


        return UserDetailsImpl.build(user,privilegesForRole);
    }






}