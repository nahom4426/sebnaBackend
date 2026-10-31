package Medco.PremuimFinancing.ServiceImpl.Messaging;

import Medco.PremuimFinancing.Entity.Messaging.EmailOutbox;
import Medco.PremuimFinancing.Repository.Messaging.EmailOutboxRepository;
import Medco.PremuimFinancing.Service.Messaging.EmailOutboxService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EmailOutboxServiceImpl implements EmailOutboxService {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxServiceImpl.class);

    private final EmailOutboxRepository emailOutboxRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${mail.custom.sender.name}")
    private String customSenderName;

    public EmailOutboxServiceImpl(EmailOutboxRepository emailOutboxRepository, JavaMailSender javaMailSender) {
        this.emailOutboxRepository = emailOutboxRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean sendOrQueue(String toEmail, String subject, String bodyHtml, String userUuid, String emailType) {
        try {
            sendNow(toEmail, subject, bodyHtml);
            EmailOutbox record = new EmailOutbox();
            record.setToEmail(toEmail);
            record.setSubject(subject);
            record.setBodyHtml(bodyHtml);
            record.setStatus(EmailOutbox.Status.SENT);
            record.setAttempts(1);
            record.setUserUuid(userUuid);
            record.setEmailType(emailType);
            emailOutboxRepository.save(record);
            return true;
        } catch (Exception e) {
            log.warn("Email send failed; queued for retry. to={} type={}", toEmail, emailType, e);
            EmailOutbox record = new EmailOutbox();
            record.setToEmail(toEmail);
            record.setSubject(subject);
            record.setBodyHtml(bodyHtml);
            record.setStatus(EmailOutbox.Status.PENDING);
            record.setAttempts(1);
            record.setNextAttemptAt(Instant.now().plus(Duration.ofHours(24)));
            record.setLastError(e.getMessage());
            record.setUserUuid(userUuid);
            record.setEmailType(emailType);
            emailOutboxRepository.save(record);
            return false;
        }
    }

    @Override
    public int processReadyToSend(int batchSize) {
        List<EmailOutbox> ready = emailOutboxRepository.findReadyToSend(
                List.of(EmailOutbox.Status.PENDING, EmailOutbox.Status.FAILED),
                Instant.now(),
                PageRequest.of(0, batchSize)
        );

        int sent = 0;
        for (EmailOutbox e : ready) {
            boolean ok = retryInternal(e);
            if (ok) sent++;
        }
        return sent;
    }

    @Override
    public boolean retryLatestForUser(String userUuid, String emailType) {
        return emailOutboxRepository.findTopByUserUuidAndEmailTypeOrderByCreatedAtDesc(userUuid, emailType)
                .map(this::retryInternal)
                .orElse(false);
    }

    @Override
    public boolean retryById(UUID outboxId) {
        return emailOutboxRepository.findById(outboxId).map(this::retryInternal).orElse(false);
    }

    @Override
    public String latestErrorForUser(String userUuid, String emailType) {
        return emailOutboxRepository.findTopByUserUuidAndEmailTypeOrderByCreatedAtDesc(userUuid, emailType)
                .map(EmailOutbox::getLastError)
                .orElse(null);
    }

    private void sendNow(String toEmail, String subject, String bodyHtml) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setFrom(username, customSenderName);
        helper.setTo(toEmail);
        helper.setText(bodyHtml, true);
        javaMailSender.send(message);
    }

    private boolean retryInternal(EmailOutbox e) {
        try {
            sendNow(e.getToEmail(), e.getSubject(), e.getBodyHtml());
            e.setStatus(EmailOutbox.Status.SENT);
            e.setLastError(null);
            e.setNextAttemptAt(null);
            e.setAttempts(e.getAttempts() + 1);
            emailOutboxRepository.save(e);
            return true;
        } catch (MailException ex) {
            e.setStatus(EmailOutbox.Status.FAILED);
            e.setAttempts(e.getAttempts() + 1);
            e.setNextAttemptAt(Instant.now().plus(Duration.ofHours(24)));
            e.setLastError(ex.getMessage());
            emailOutboxRepository.save(e);
            return false;
        } catch (RuntimeException ex) {
            e.setStatus(EmailOutbox.Status.FAILED);
            e.setAttempts(e.getAttempts() + 1);
            e.setNextAttemptAt(Instant.now().plus(Duration.ofHours(24)));
            e.setLastError(ex.getMessage());
            emailOutboxRepository.save(e);
            return false;
        } catch (Exception ex) {
            e.setStatus(EmailOutbox.Status.FAILED);
            e.setAttempts(e.getAttempts() + 1);
            e.setNextAttemptAt(Instant.now().plus(Duration.ofHours(24)));
            e.setLastError(ex.getMessage());
            emailOutboxRepository.save(e);
            return false;
        }
    }
}
