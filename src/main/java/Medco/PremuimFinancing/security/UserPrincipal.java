package Medco.PremuimFinancing.security;//package com.medcoAnalytics.invoiceService.security;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.medcoAnalytics.invoiceService.entity.User.UserEntity;
//import com.medcoAnalytics.invoiceService.enums.Stock.PharmacyPersonnelType;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//
//@Getter
//@Setter
//public class UserPrincipal implements UserDetails {
//
//    @Getter
//    private String userUuid;
//    private String name;
//    @Getter
//    private  String email;
//    @JsonIgnore
//    private String password;
//    private boolean accountNonLocked;
//    private Collection<? extends GrantedAuthority> authorities;
//    private String pharmacyUuid;
//    private PharmacyPersonnelType pharmacyPersonnelType;
//    private String stockLocationName;
//
//
//    public UserPrincipal(String userUuid, String email, String password, String name,String pharmacyUuid,PharmacyPersonnelType pharmacyPersonnelType,
//                         List<GrantedAuthority> authorities) {
//        this.userUuid=userUuid;
//        this.email = email;
//        this.password= password;
//        this.name = name;
//        this.authorities = authorities;
//        this.pharmacyUuid= pharmacyUuid;
//        this.pharmacyPersonnelType =pharmacyPersonnelType;
//       // this.accountNonLocked = accountNonLocked;
//    }
//
//    public static UserDetails build(UserEntity user, List<GrantedAuthority> authorities) {
//        return new UserPrincipal(user.getUserUuid(), user.getEmail(), user.getPassword(),user.getFirstName(), user.getPharmacyUuid(),user.getPharmacyPersonnelType(), authorities);
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (o == null || getClass() != o.getClass())
//            return false;
//        UserPrincipal user = (UserPrincipal) o;
//        return Objects.equals(userUuid, user.userUuid);
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
