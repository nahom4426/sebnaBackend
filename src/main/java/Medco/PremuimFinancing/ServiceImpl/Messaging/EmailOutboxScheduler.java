package Medco.PremuimFinancing.ServiceImpl.Messaging;

import Medco.PremuimFinancing.Service.Messaging.EmailOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailOutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxScheduler.class);

    private final EmailOutboxService emailOutboxService;

    public EmailOutboxScheduler(EmailOutboxService emailOutboxService) {
        this.emailOutboxService = emailOutboxService;
    }

    @Scheduled(fixedDelayString = "${email.outbox.retry.fixedDelayMs:86400000}")
    public void retryPendingEmails() {
        int processed = emailOutboxService.processReadyToSend(50);
        if (processed > 0) {
            log.info("Email outbox retry processed: {} sent", processed);
        }
    }
}
