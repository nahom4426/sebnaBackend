package Medco.PremuimFinancing.ServiceImpl.User;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Enums.User.Status;
import Medco.PremuimFinancing.Enums.User.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = -8544780218833555579L;

    private String userUuid;
    private String email;

    @JsonIgnore
    private String password;

    private String roleUuid;
    private String roleName;
    private String title;
    private String firstName;
    private String fatherName;
    private String grandFatherName;
    private String gender;
    private String mobilePhone;
    private Status userStatus;
    private UserType userType;
    private String profilePicture;



    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String userUuid, String email, String password, String roleUuid, String roleName , String title, String firstName,
                           String fatherName, String grandFatherName, String gender, String mobilePhone, Status userStatus, UserType userType, String profilePicture,
                           Collection<? extends GrantedAuthority> authorities) {
        this.userUuid = userUuid;
        this.email = email;
        this.password = password;
        this.roleUuid = roleUuid;
        this.roleName = roleName;
        this.title = title;
        this.firstName = firstName;
        this.fatherName = fatherName;
        this.grandFatherName = grandFatherName;
        this.gender = gender;
        this.mobilePhone = mobilePhone;
        this.userStatus = userStatus;
        this.userType = userType;
        this.profilePicture = profilePicture;

        this.authorities = authorities;


    }

    // Factory method to build UserDetailsImpl from a UserEntity and privileges list
    public static UserDetailsImpl build(UserEntity user, List<String> privilegesForRole) {
        List<GrantedAuthority> authorities = privilegesForRole.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Convert imageData to base64 string for profilePicture if available
        String profilePictureStr = null;
        if (user.getImageData() != null) {
            profilePictureStr = java.util.Base64.getEncoder().encodeToString(user.getImageData());
        } else if (user.getProfilePicture() != null) {
            profilePictureStr = user.getProfilePicture();
        }

        return new UserDetailsImpl(user.getUserUuid(), user.getEmail(), user.getPassword(), user.getRole().getRoleUuid(), user.getRole().getRoleName(),
                user.getTitle(), user.getFirstName(), user.getFatherName(), user.getGrandFatherName(), user.getGender(),
                user.getMobilePhone(), user.getUserStatus(), user.getUserType(),
                profilePictureStr, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(userUuid, user.userUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUuid);
    }
}
