package Medco.PremuimFinancing.security;//package com.medcoAnalytics.invoiceService.security;
//
//
//import com.medcoAnalytics.invoiceService.entity.User.UserEntity;
//import com.medcoAnalytics.invoiceService.entity.UserRole.Role;
//import com.medcoAnalytics.invoiceService.repository.Role.PrivilegeRepository;
//import com.medcoAnalytics.invoiceService.repository.Role.RoleRepository;
//import com.medcoAnalytics.invoiceService.repository.User.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CustomUserDetails  implements UserDetailsService {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
//
//    @Autowired
//    private UserRepository repository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private PrivilegeRepository privilegeRepository;
//
//    @Override
//   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity user = repository.findByEmail(username);
////        UserEntity user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with Email " + username + " Not found."));
//
//        Role rolePrivilege = roleRepository.findByRoleUuid(user.getRoleUuid());
//
//        assert rolePrivilege != null;
//        List<String> privilegesForRole = new ArrayList<>();
//
//        //List<Privileges> privileges = privilegeRepository.findAllById()
//
//        List<String> privilegesForRoles = rolePrivilege.getPrivileges()
//                .stream()
//                .map(privileges -> {
//                    privilegesForRole.add("ROLE_" + privileges.getPrivilegeName());
//                     return "ROLE_" + privileges.getPrivilegeName();
//                }).toList();
//
//        List<GrantedAuthority> authorities = privilegesForRole.stream().map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//        return UserPrincipal.build(user,authorities);
//
//    }
//}
