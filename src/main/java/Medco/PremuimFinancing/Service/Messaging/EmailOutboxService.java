package Medco.PremuimFinancing.Service.Messaging;

import java.util.UUID;

public interface EmailOutboxService {
    boolean sendOrQueue(String toEmail, String subject, String bodyHtml, String userUuid, String emailType);

    int processReadyToSend(int batchSize);

    boolean retryLatestForUser(String userUuid, String emailType);

    boolean retryById(UUID outboxId);
}
