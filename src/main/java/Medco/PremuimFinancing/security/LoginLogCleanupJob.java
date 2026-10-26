package Medco.PremuimFinancing.security;

import Medco.PremuimFinancing.Repository.Security.LoginLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class LoginLogCleanupJob {

    private final LoginLogRepository loginLogRepository;

    private final int retentionDays;

    public LoginLogCleanupJob(LoginLogRepository loginLogRepository,
                              @Value("${sebna.logs.login.retention-days:90}") int retentionDays) {
        this.loginLogRepository = loginLogRepository;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${sebna.logs.login.cleanup-cron:0 0 2 * * *}")
    @SuppressWarnings("unused")
    public void purgeOldLoginLogs() {
        if (retentionDays <= 0) {
            return;
        }
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        loginLogRepository.deleteOlderThan(cutoff);
    }
}
