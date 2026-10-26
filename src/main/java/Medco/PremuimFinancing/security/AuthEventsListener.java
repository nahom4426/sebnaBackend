package Medco.PremuimFinancing.security;

import Medco.PremuimFinancing.Entity.Security.LoginLog;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Security.LoginLogRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthEventsListener {

    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;

    public AuthEventsListener(LoginLogRepository loginLogRepository, UserRepository userRepository) {
        this.loginLogRepository = loginLogRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        HttpServletRequest request = currentRequest();
        UserEntity user = userRepository.findByEmailOrMobilePhone(username, username);

        LoginLog log = new LoginLog();
        log.setEmail(username);
        log.setUserUuid(user != null ? user.getUserUuid() : null);
        log.setSuccess(true);
        log.setIpAddress(extractIp(request));
        log.setUserAgent(extractUserAgent(request));
        loginLogRepository.save(log);
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        HttpServletRequest request = currentRequest();

        LoginLog log = new LoginLog();
        log.setEmail(username);
        log.setSuccess(false);
        log.setIpAddress(extractIp(request));
        log.setUserAgent(extractUserAgent(request));
        log.setFailureReason(event.getException() != null ? event.getException().getClass().getSimpleName() : null);
        loginLogRepository.save(log);
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String extractIp(HttpServletRequest request) {
        if (request == null) return null;
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) return null;
        return request.getHeader("User-Agent");
    }
}
