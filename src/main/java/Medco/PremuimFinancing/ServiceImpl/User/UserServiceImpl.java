package Medco.PremuimFinancing.ServiceImpl.User;

import Medco.PremuimFinancing.Dto.Request.Password.ChangePasswordRequest;
import Medco.PremuimFinancing.Dto.Request.Password.ResetPasswordRequest;
import Medco.PremuimFinancing.Dto.Request.User.AuthRequest;
import Medco.PremuimFinancing.Dto.Request.User.SignUpRequest;
import Medco.PremuimFinancing.Dto.Request.User.SebnaSignUpRequest;
import Medco.PremuimFinancing.Dto.Request.User.UserRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.ProfilePcrure.ProfileResponse;
import Medco.PremuimFinancing.Dto.Response.User.ClientMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.ClientResponse;
import Medco.PremuimFinancing.Dto.Response.User.JwtResponse;
import Medco.PremuimFinancing.Dto.Response.User.MyShareStatsResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Enums.User.Status;
import Medco.PremuimFinancing.Enums.User.UserType;
import Medco.PremuimFinancing.Repository.Role.RoleRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Messaging.EmailOutboxService;
import Medco.PremuimFinancing.Service.MessagingService;
import Medco.PremuimFinancing.Service.Settings.AppSettingService;
import Medco.PremuimFinancing.Service.User.UserService;
import Medco.PremuimFinancing.exception.BadRequestException;
import Medco.PremuimFinancing.exception.EmailAlreadyExists;
import Medco.PremuimFinancing.exception.InvalidPhoneException;
import Medco.PremuimFinancing.exception.ResourceNotFoundException;
import Medco.PremuimFinancing.security.JwtUtils;
import Medco.PremuimFinancing.security.LoginAttemptService;
import Medco.PremuimFinancing.utils.Image.ImageUtils;
import Medco.PremuimFinancing.utils.PasswordGenerator;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository authRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final PasswordEncoder encoder;
    private final MessagingService messagingService;
    private final JwtUtils jwtUtils;
    private final LoginAttemptService loginAttemptService;
    private final EmailOutboxService emailOutboxService;
    private final AppSettingService appSettingService;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${mail.custom.sender.name}")
    private String customSenderName;

    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository authRepository, RoleRepository roleRepository, JavaMailSender javaMailSender, TemplateEngine templateEngine, PasswordEncoder passwordEncoder, PasswordEncoder encoder, MessagingService messagingService, JwtUtils jwtUtils, LoginAttemptService loginAttemptService, EmailOutboxService emailOutboxService, AppSettingService appSettingService) {
        this.authenticationManager = authenticationManager;
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.encoder = encoder;
        this.messagingService = messagingService;
        this.jwtUtils = jwtUtils;
        this.loginAttemptService = loginAttemptService;
        this.emailOutboxService = emailOutboxService;
        this.appSettingService = appSettingService;
    }

    private UserEntity resolveUserByUuidOrId(String userUuidOrId) {
        if (userUuidOrId != null && userUuidOrId.matches("\\d+")) {
            return authRepository.findById(Long.parseLong(userUuidOrId))
                    .orElseThrow(() -> new BadRequestException("client not found"));
        }
        return authRepository.findByUserUuid(userUuidOrId)
                .orElseThrow(() -> new BadRequestException("client not found"));
    }

    private boolean isSuperAdmin(UserEntity user) {
        return user.getRole() != null && (
                "SuperAdmin".equalsIgnoreCase(user.getRole().getRoleName()) ||
                "super_admin".equalsIgnoreCase(user.getRole().getRoleName())
        );
    }

    private boolean isShareholder(UserEntity user) {
        return user.getRole() != null && "Shareholders".equalsIgnoreCase(user.getRole().getRoleName());
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal costBasisPricePerShare(UserEntity u) {
        BigDecimal p = u.getPurchasePricePerShare();
        if (p != null && p.compareTo(BigDecimal.ZERO) > 0) {
            return p;
        }
        return safe(appSettingService.getPricePerShare());
    }

    private BigDecimal totalCost(UserEntity u) {
        BigDecimal sharesQty = safe(u.getSharesBoughtAmount());
        BigDecimal pricePerShare = costBasisPricePerShare(u);
        return sharesQty.multiply(pricePerShare);
    }

    private BigDecimal remaining(UserEntity u) {
        return totalCost(u).subtract(safe(u.getAmountPaid()));
    }

    private void validateSharesQty(BigDecimal sharesQty) {
        if (sharesQty == null) {
            throw new BadRequestException("sharesBoughtAmount is required");
        }
        if (sharesQty.compareTo(new BigDecimal("5")) < 0) {
            throw new BadRequestException("Minimum shares to buy is 5");
        }
        if (sharesQty.stripTrailingZeros().scale() > 0) {
            throw new BadRequestException("sharesBoughtAmount must be a whole number (quantity of shares)");
        }
    }

    private UserEntity getActor() {
        var auth = SecurityUtils.getAuthenticatedUser();
        return authRepository.findByUserUuid(auth.getUserUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private void ensureSuperAdmin(UserEntity actor) {
        if (!isSuperAdmin(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
    }

     @Override
     public MyShareStatsResponse getMyShareStats() {
         var auth = SecurityUtils.getAuthenticatedUser();
         UserEntity user = authRepository.findByUserUuid(auth.getUserUuid())
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

         MyShareStatsResponse res = new MyShareStatsResponse();
         res.setUserUuid(user.getUserUuid());
         res.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
         res.setFirstName(user.getFirstName());
         res.setFatherName(user.getFatherName());
         res.setUserName(user.getUserName());
         res.setProfilePicture(auth.getProfilePicture());

         BigDecimal shares = safe(user.getSharesBoughtAmount());

         BigDecimal currentPricePerShare = safe(appSettingService.getPricePerShare());
         BigDecimal purchasePricePerShare = costBasisPricePerShare(user);

         BigDecimal total = shares.multiply(purchasePricePerShare);
         BigDecimal paid = safe(user.getAmountPaid());
         BigDecimal remaining = total.subtract(paid);
         if (remaining.compareTo(BigDecimal.ZERO) < 0) {
             remaining = BigDecimal.ZERO;
         }

         BigDecimal progress;
         if (total.compareTo(BigDecimal.ZERO) <= 0) {
             progress = BigDecimal.ZERO;
         } else {
             progress = paid
                     .multiply(new BigDecimal("100"))
                     .divide(total, 2, RoundingMode.HALF_UP);
         }

         res.setSharesBoughtAmount(shares);
         res.setPricePerShare(currentPricePerShare);
         res.setPurchasePricePerShare(purchasePricePerShare);
         res.setTotalCost(total);
         res.setAmountPaid(paid);
         res.setAmountRemaining(remaining);
         res.setPaymentProgressPercent(progress);

         BigDecimal marketValue = shares.multiply(currentPricePerShare);
         BigDecimal gainAmount = marketValue.subtract(total);
         BigDecimal gainPercent;
         if (total.compareTo(BigDecimal.ZERO) <= 0) {
             gainPercent = BigDecimal.ZERO;
         } else {
             gainPercent = gainAmount
                     .multiply(new BigDecimal("100"))
                     .divide(total, 2, RoundingMode.HALF_UP);
         }
         res.setCurrentMarketValue(marketValue);
         res.setValueIncreaseAmount(gainAmount);
         res.setValueIncreasePercent(gainPercent);

         if (total.compareTo(BigDecimal.ZERO) <= 0) {
             res.setPaymentStatus("NO_SHARES");
         } else if (paid.compareTo(total) >= 0) {
             res.setPaymentStatus("FULLY_PAID");
         } else {
             res.setPaymentStatus("OUTSTANDING");
         }

         return res;
     }

    @Override
    public ResponseEntity<?> AuthenticateUser(AuthRequest authRequest) {

        if (loginAttemptService.isBlocked(username)) {
            throw new RuntimeException("User temporarily locked due to too many failed login attempts,please attempt after 3 minutes");
        }
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            loginAttemptService.loginSucceeded(username);
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            throw e;
        }

        UserEntity user = authRepository.findByEmailOrMobilePhone(authRequest.getEmail(), authRequest.getEmail());
        if (user == null) throw new ResourceNotFoundException("user " + authRequest.getEmail() + " not found");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("privileges " + userDetails.getAuthorities().toString());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (user.getUserStatus() != Status.ACTIVE)
            throw new BadRequestException("your account is " + user.getUserStatus());

        String insuredUuid = (userDetails.getUserType() == UserType.Client) ? userDetails.getUserUuid() : null;
        String institutionId = null;
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUserUuid(), insuredUuid, institutionId, userDetails.getEmail(), userDetails.getRoleUuid(), userDetails.getRoleName(),
                userDetails.getTitle(), userDetails.getFirstName(), userDetails.getFatherName(),
                userDetails.getGrandFatherName(), userDetails.getGender(), userDetails.getMobilePhone(),
                userDetails.getUserStatus(), userDetails.getUserType(), userDetails.getProfilePicture(),
                user.getSharesBoughtAmount(), safe(appSettingService.getPricePerShare()), totalCost(user), user.getAmountPaid(), remaining(user),
                roles));
    }

    @Transactional
    @Override
    public ResponseEntity<?> Save(SignUpRequest signUpRequest) {

        if (authRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExists("Email is Already in use");
        }

        if (authRepository.existsByMobilePhone(signUpRequest.getMobilePhone())) {
            throw new InvalidPhoneException("Phone is Already In use");
        }

        Role role = roleRepository.findByRoleUuid(signUpRequest.getRoleUuid());
        if (role == null) throw new BadRequestException("No role name was found with the provided RoleUuid");

        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(signUpRequest, user);
        user.setRole(role);

        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setUserStatus(Status.INACTIVE);

        UserEntity savedUser = authRepository.save(user);

        try {
            ResponseEntity<?> otpResult = generateAndSendOtp(savedUser.getEmail());
            if (otpResult != null && otpResult.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("User Registered Successfully. Verify Your Email Please");
            }
        } catch (Exception e) {
            log.warn("Failed to send OTP email during signup for {}", savedUser.getEmail(), e);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User Registered Successfully. Unable to send verification code right now. Please try again later.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> sebnaSignUp(SebnaSignUpRequest signUpRequest) {

        UserEntity actor = getActor();

        ensureSuperAdmin(actor);

        if (authRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExists("Email is Already in use");
        }

        if (authRepository.existsByMobilePhone(signUpRequest.getMobilePhone())) {
            throw new InvalidPhoneException("Phone is Already In use");
        }

        Role role = roleRepository.findByRoleUuid(signUpRequest.getRoleUuid());
        if (role == null) throw new BadRequestException("No role name was found with the provided RoleUuid");

        String roleName = role.getRoleName() != null ? role.getRoleName() : "";
        if (!"Shareholders".equalsIgnoreCase(roleName) && !"SuperAdmin".equalsIgnoreCase(roleName)) {
            throw new BadRequestException("Invalid role: " + roleName);
        }

        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(signUpRequest, user);
        user.setRole(role);

        if ("Shareholders".equalsIgnoreCase(roleName)) {
            BigDecimal sharesAmount = safe(signUpRequest.getSharesBoughtAmount());
            BigDecimal paid = safe(signUpRequest.getAmountPaid());
            validateSharesQty(sharesAmount);
            if (paid.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("amountPaid cannot be negative");
            }

            BigDecimal currentPrice = safe(appSettingService.getPricePerShare());
            BigDecimal total = sharesAmount.multiply(currentPrice);
            if (paid.compareTo(total) > 0) {
                throw new BadRequestException("amountPaid cannot exceed total cost");
            }
            user.setSharesBoughtAmount(sharesAmount);
            user.setAmountPaid(paid);
            if (user.getPurchasePricePerShare() == null || user.getPurchasePricePerShare().compareTo(BigDecimal.ZERO) <= 0) {
                user.setPurchasePricePerShare(currentPrice);
            }
        }

        String generatedPassword = PasswordGenerator.generatePassword();
        user.setPassword(encoder.encode(generatedPassword));
        user.setCreatedAt(Instant.now());
        user.setUserStatus(Status.ACTIVE);

        UserEntity savedUser = authRepository.save(user);

        boolean passwordEmailSent = sendPasswordEmail(savedUser.getEmail(), generatedPassword, savedUser.getFirstName(), savedUser.getUserUuid(), savedUser);

        if (passwordEmailSent) {
            return ResponseEntity.ok("User Registered Successfully. Password sent to your email");
        }
        return ResponseEntity.ok("User Registered Successfully. Unable to send password email right now.");
    }

    @Override
    public ResponseEntity<MessageResponse> updateUser(String userUuid, UserRequest userRequest) {

        UserEntity actor = getActor();
        UserEntity user = resolveUserByUuidOrId(userUuid);

        if (user == null)
            throw new RuntimeException("User not found.");

        ensureSuperAdmin(actor);

        BeanUtils.copyProperties(userRequest, user, "roleUuid");

        Role role = roleRepository.findByRoleUuid(userRequest.getRoleUuid());
        if (role == null) throw new BadRequestException("No role name was found with the provided RoleUuid");
        user.setRole(role);

        if (role.getRoleName() != null && "Shareholders".equalsIgnoreCase(role.getRoleName())) {
            BigDecimal sharesQty = safe(userRequest.getSharesBoughtAmount());
            BigDecimal paid = safe(userRequest.getAmountPaid());
            validateSharesQty(sharesQty);
            if (paid.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("amountPaid cannot be negative");
            }
            BigDecimal currentPrice = safe(appSettingService.getPricePerShare());
            BigDecimal purchasePrice = costBasisPricePerShare(user);
            BigDecimal total = sharesQty.multiply(purchasePrice);
            if (paid.compareTo(total) > 0) {
                throw new BadRequestException("amountPaid cannot exceed total cost");
            }
            user.setSharesBoughtAmount(sharesQty);
            user.setAmountPaid(paid);
            if (user.getPurchasePricePerShare() == null || user.getPurchasePricePerShare().compareTo(BigDecimal.ZERO) <= 0) {
                user.setPurchasePricePerShare(currentPrice);
            }
        }

        UserEntity saved = authRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User Updated successfully!"));
    }

    @Override
    public ResponseEntity<?> deleteUser(String userUuid) {

        UserEntity actor = getActor();
        UserEntity user = resolveUserByUuidOrId(userUuid);
        if (user == null)
            throw new RuntimeException("User not found.");

        ensureSuperAdmin(actor);
        user.setDeleted(true);
        authRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User soft deleted successfully!"));
    }

    @Override
    public UserResponse getUser(String userUuid) {

        UserEntity actor = getActor();
        UserEntity user = resolveUserByUuidOrId(userUuid);

        ensureSuperAdmin(actor);

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        userResponse.setRoleName(user.getRole().getRoleName());
        userResponse.setRoleUuid(user.getRole().getRoleUuid());
        if (isShareholder(user)) {
            userResponse.setPricePerShare(safe(appSettingService.getPricePerShare()));
            userResponse.setTotalCost(totalCost(user));
        }
        userResponse.setAmountRemaining(remaining(user));
        return userResponse;
    }

    @Override
    public ResponseEntity<?> sendPasswordResetCode(String email) {
        try {
            Optional<UserEntity> userByEmail = Optional.ofNullable(authRepository.findByEmail(email));
            if (userByEmail.isEmpty())
                throw new RuntimeException("User not found.");

            String token = generateSixDigitNumber();

//            createPasswordResetTokenForUser(userByEmail.get(), token);
            userByEmail.get().setPasswordResetCode(token);
            authRepository.save(userByEmail.get());

            sendEmailToUser(email, token);

        } catch (MessagingException e) {
            throw new BadRequestException("Email is Already sent Please check your Email!");
        } catch (jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new MessageResponse("Password Reset Code Sent Successfully!"));
    }

    private void sendEmailToUser(String email, String token) throws jakarta.mail.MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("resetUrl", token);
        context.setVariable("header", "Password Reset Request");

        UserEntity forEmail = authRepository.findByEmail(email);
        if (forEmail == null) throw new BadRequestException("No User Found With The Provided Email");

        context.setVariable("userName", forEmail.getFirstName());

        String content = templateEngine.process("reset-password.html", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Set true for multipart message
        helper.setSubject(" Your Password Reset Request: " + email);
        helper.setFrom(username, customSenderName);
        helper.setTo(email);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    private boolean sendPasswordEmail(String email, String password, String firstName, String userUuid) {
        try {
            Context context = new Context();
            context.setVariable("userName", firstName);
            context.setVariable("email", email);
            context.setVariable("password", password);
            context.setVariable("header", "Welcome to Sebna - Your Account is Ready!");

            String content = templateEngine.process("welcome-password.html", context);
            // try now; if it fails, it will be queued for retry
            return emailOutboxService.sendOrQueue(
                    email,
                    "Welcome to Sebna - Your Account Credentials",
                    content,
                    userUuid,
                    "WELCOME_PASSWORD"
            );
        } catch (Exception e) {
            log.warn("Failed to prepare welcome password email for {}", email, e);
            return false;
        }
    }

    private boolean sendPasswordEmail(String email, String password, String firstName, String userUuid, UserEntity createdUser) {
        try {
            Context context = new Context();
            context.setVariable("userName", firstName);
            context.setVariable("email", email);
            context.setVariable("password", password);
            context.setVariable("header", "Welcome to Sebna - Your Account is Ready!");

            if (createdUser != null && isShareholder(createdUser)) {
                context.setVariable("sharesBoughtAmount", safe(createdUser.getSharesBoughtAmount()));
                context.setVariable("pricePerShare", safe(appSettingService.getPricePerShare()));
                context.setVariable("totalCost", totalCost(createdUser));
                context.setVariable("amountPaid", safe(createdUser.getAmountPaid()));
                context.setVariable("amountRemaining", remaining(createdUser));
            }

            String content = templateEngine.process("welcome-password.html", context);
            return emailOutboxService.sendOrQueue(
                    email,
                    "Welcome to Sebna - Your Account Credentials",
                    content,
                    userUuid,
                    "WELCOME_PASSWORD"
            );
        } catch (Exception e) {
            log.warn("Failed to prepare welcome password email for {}", email, e);
            return false;
        }
    }

    @Override
    public ResponseEntity<?> adminRetryWelcomePasswordEmail(String userUuid) {
        boolean ok = emailOutboxService.retryLatestForUser(userUuid, "WELCOME_PASSWORD");
        if (ok) return ResponseEntity.ok(new MessageResponse("Email sent successfully"));
        String reason = emailOutboxService.latestErrorForUser(userUuid, "WELCOME_PASSWORD");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(reason == null
                        ? "Email not sent. No welcome email was found for this user."
                        : "Email not sent: " + reason));
    }

    @Override
    public ResponseEntity<?> adminResetUserPassword(String userUuid) {
        UserEntity user = authRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new BadRequestException("client not found"));

        String generatedPassword = PasswordGenerator.generatePassword();
        user.setPassword(encoder.encode(generatedPassword));
        authRepository.save(user);

        try {
            Context context = new Context();
            context.setVariable("userName", user.getFirstName());
            context.setVariable("email", user.getEmail());
            context.setVariable("password", generatedPassword);
            context.setVariable("header", "Welcome to Sebna - Your Account is Ready!");
            String content = templateEngine.process("welcome-password.html", context);
            boolean sent = emailOutboxService.sendOrQueue(
                    user.getEmail(),
                    "Welcome to Sebna - Your Account Credentials",
                    content,
                    user.getUserUuid(),
                    "WELCOME_PASSWORD"
            );
            if (sent) {
                return ResponseEntity.ok(new MessageResponse("Password reset and email sent"));
            }
            return ResponseEntity.ok(new MessageResponse("Password reset. Email queued for retry."));
        } catch (Exception e) {
            log.warn("Password reset succeeded, but email enqueue failed for {}", user.getUserUuid(), e);
            return ResponseEntity.ok(new MessageResponse("Password reset. Email enqueue failed."));
        }
    }

    private String generateSixDigitNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);

        return String.format("%06d", number);
    }

    @Override
    public UserMyResponse getUsers(String search, Pageable pageable) {
        UserEntity actor = getActor();

        ensureSuperAdmin(actor);

        Page<UserEntity> usersPage = search != null
                ? authRepository.findByMobilePhoneStartingWithOrFirstNameStartingWithIgnoreCase(search, search, pageable)
                : authRepository.findAll(pageable);
        long totalPages = usersPage.getTotalPages();
        long totalElements = usersPage.getTotalElements();
        int pageNumber = usersPage.getNumber();

        List<UserEntity> userList = usersPage.getContent().stream()
                .filter(userEntity -> userEntity.getRole() != null)
                .filter(userEntity -> !userEntity.isDeleted()).toList();

        List<UserResponse> userResponseList = new ArrayList<>();
        for (UserEntity u : userList) {
            UserResponse ur = new UserResponse();
            BeanUtils.copyProperties(u, ur);
            ur.setRoleName(u.getRole().getRoleName());
            ur.setRoleUuid(u.getRole().getRoleUuid());
            if (isShareholder(u)) {
                ur.setPricePerShare(safe(appSettingService.getPricePerShare()));
                ur.setTotalCost(totalCost(u));
            }
            ur.setAmountRemaining(remaining(u));
            userResponseList.add(ur);
        }

        UserMyResponse response = new UserMyResponse();
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setPageNumber(pageNumber);
        response.setResponse(userResponseList);
        return response;
    }

    @Override
    public List<UserResponse> getPayerUsers(int page, int limit) {

        return null;
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail) {

        if (resetPasswordDetail.getEmail() == null || resetPasswordDetail.getEmail().isBlank()) {
            throw new RuntimeException("email is required.");
        }
        if (resetPasswordDetail.getVerificationCode() == null || resetPasswordDetail.getVerificationCode().isBlank()) {
            throw new RuntimeException("verificationCode is required.");
        }

        UserEntity userEntity = authRepository.findByEmail(resetPasswordDetail.getEmail());
        if (userEntity == null) throw new RuntimeException("email is not correct.");

        if (userEntity.getPasswordResetCode() == null
                || !userEntity.getPasswordResetCode().equals(resetPasswordDetail.getVerificationCode())) {
            throw new RuntimeException("Invalid Reset Code.");
        }

        if (!resetPasswordDetail.getNewPassword().equals(resetPasswordDetail.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match.");

        userEntity.setPassword(encoder.encode(resetPasswordDetail.getNewPassword()));
        userEntity.setPasswordResetCode(null);
        authRepository.save(userEntity);
        String returnValue = "Password changed successfully";

        return ResponseEntity.ok(new MessageResponse(returnValue));
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest resetPasswordDetail, String userUuid) {


        var user = authRepository.findByUserUuid(userUuid).orElseThrow(()->new BadRequestException("client not found"));
        if (user == null)
            throw new BadRequestException("Can't find User with the Provided Id");

        System.out.println("\n\n\n change password logic "+ user.getEmail());

        if(passwordEncoder.matches(resetPasswordDetail.getOldPassword(), user.getPassword())){
            if (resetPasswordDetail.getNewPassword().equals(resetPasswordDetail.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(resetPasswordDetail.getNewPassword()));

                authRepository.save(user);
            } else {
                throw new BadRequestException("password Doesn't match with confirm password");
            }
        }else {
            throw new BadRequestException("Please Enter your valid password to change it!");
        }

        return ResponseEntity.ok("Password Changed Successfully");




    }

    @Override
    public ResponseEntity<?> verifyAccount(String emailVerificationToken) {
        String returnValue = "";
        UserEntity user = authRepository.findByEmailVerificationToken(emailVerificationToken);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setUserStatus(Status.ACTIVE);
        UserEntity updatedUser = authRepository.save(user);
        if (updatedUser.getUserStatus() == Status.ACTIVE) {
            returnValue = "Account Verified Successfully";
        }
        return ResponseEntity.ok(new MessageResponse(returnValue));
    }

    @Override
    public ResponseEntity <?> checkResetCode(String verificationCode, String email) {
        UserEntity userEntity = authRepository.findByEmailAndPasswordResetCode(email,verificationCode);
        if(userEntity == null) throw new RuntimeException("Invalid Reset Code.");
        return ResponseEntity.ok(new MessageResponse("Reset Code is valid"));

    }

    @Override
    public List<UserResponse> searchUsers(String searchKey, int page, int limit) {

        UserEntity actor = getActor();

        ensureSuperAdmin(actor);

        if(page > 0) page = page - 1;
        String[] searchKeys = searchKey.split(" ");

        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        int countSpaces = StringUtils.countOccurrencesOf(searchKey, " ");

        Page<UserEntity> usersPage = null;

        if(countSpaces == 0) {
            usersPage = authRepository.searchUsersIgnoreCase(searchKey, pageableRequest);
        }

        else if(countSpaces == 1){
            String firstName = searchKeys[0];
            String fatherName = searchKeys[1];
            usersPage = authRepository.findByFirstNameContainingIgnoreCaseAndFatherNameContainingIgnoreCase(firstName,fatherName,pageableRequest);
        }

        else if(countSpaces == 2) {
            String firstName = searchKeys[0];
            String fatherName = searchKeys[1];
            String grandFatherName = searchKeys[2];
            usersPage = authRepository.findByFirstNameContainingIgnoreCaseAndFatherNameContainingIgnoreCaseAndGrandFatherNameContainingIgnoreCase(firstName,fatherName,grandFatherName,pageableRequest);
        }


        assert usersPage != null;
        List<UserEntity> users = usersPage.getContent();
        List<UserResponse> userResponse = new ArrayList<>();

        for (UserEntity u : users) {
            if (u == null || u.isDeleted()) {
                continue;
            }
            UserResponse ur = new UserResponse();
            BeanUtils.copyProperties(u, ur);
            ur.setRoleName(u.getRole() != null ? u.getRole().getRoleName() : null);
            ur.setRoleUuid(u.getRole() != null ? u.getRole().getRoleUuid() : null);
            if (isShareholder(u)) {
                ur.setPricePerShare(safe(appSettingService.getPricePerShare()));
                ur.setTotalCost(totalCost(u));
            }
            ur.setAmountRemaining(remaining(u));
            userResponse.add(ur);
        }

        return userResponse;
    }

    @Override
    public ResponseEntity<?> editProfile(String userUuid, String userName, MultipartFile profilePicture) throws IOException {

        UserEntity user = authRepository.findByUserUuid(userUuid).orElseThrow(()->new BadRequestException("client not found"));


        if (profilePicture==null){

            user.setUserName(userName);
        }else {

            byte[] profilePictureBytes = profilePicture.getBytes();

            if (profilePictureBytes.length > 1048576)
                throw new BadRequestException("Profile should be less than 1mb");

            user.setImageData(ImageUtils.compressImage(profilePictureBytes));
            user.setUserName(userName);
        }
        authRepository.save(user);

        return ResponseEntity.ok("profile changed successfully");

    }

    @Override
    public ProfileResponse getProfile(String userUuid) {
        UserEntity profile = authRepository.findByUserUuid(userUuid).orElseThrow(()->new BadRequestException("client not found"));
        if (profile == null) {
            return null;
        }

        ProfileResponse response = new ProfileResponse();
        if (profile.getUserName()==null||profile.getUserName().isEmpty())
         response.setUserName(profile.getFirstName());
        else response.setUserName(profile.getUserName());

        byte[] decompressedData;

        if (profile.getImageData()==null) {
            decompressedData = null;

        }
        else {
            decompressedData = ImageUtils.decompressImage(profile.getImageData());

        }
        response.setImageData(decompressedData);



        return response;
    }

    @Override
    public ClientMyResponse getClients(String search, Pageable pageable) {
        Page<UserEntity> clientsPage = authRepository.findAll(pageable);

        List<ClientResponse> responseList = clientsPage.getContent().stream()
                .filter(user -> user.getUserType() == UserType.Client)
                .map(userEntity -> {
                    ClientResponse response = new ClientResponse();
                    response.setClientName(userEntity.getTitle() + " " + userEntity.getFirstName() + " " + userEntity.getFatherName() + " " + userEntity.getGrandFatherName());
                    response.setPhoneNumber(userEntity.getMobilePhone());
                    if (userEntity.getCreatedAt() != null) {
                        response.setRegistrationDate(Date.from(userEntity.getCreatedAt()));
                    }
                    BeanUtils.copyProperties(userEntity, response);
                    return response;
                }).toList();

        ClientMyResponse paged = new ClientMyResponse();
        paged.setTotalPages(clientsPage.getTotalPages());
        paged.setTotalElements(clientsPage.getTotalElements());
        paged.setPageNumber(clientsPage.getNumber());
        paged.setResponse(responseList);
        return paged;
    }

    @Override
    public ResponseEntity<?> verifyCode(String email, String code) {
        UserEntity user=authRepository.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        if (code.equals( user.getOtp())){
            user.setUserStatus(Status.ACTIVE);
            authRepository.save(user);
            return ResponseEntity.ok("account verified successfully");
        }
        else throw new BadRequestException("please enter correct code");

    }

    @Override
    public ResponseEntity<?> generateAndSendOtp(String email) {
        UserEntity user=authRepository.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        if (user.getUserStatus()==Status.ACTIVE)
            throw new BadRequestException("your account is active just login");
        String otp=generateOtp();
        user.setOtp(otp);
        authRepository.save(user);
        System.out.println("the otp is "+otp);
        return messagingService.sendOtp(user,otp);


    }

    @Override
    public ResponseEntity<?> generateAndSendSms(String userUuid,String message) {
        UserEntity user=authRepository.findByUserUuid(userUuid).orElseThrow(()->new BadRequestException("user not found"));
        return messagingService.sendSms(user,message);

    }


    private String generateOtp () {

        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(10000); // Generates 0 - 9999
        return String.format("%04d", otp);
    }

}

