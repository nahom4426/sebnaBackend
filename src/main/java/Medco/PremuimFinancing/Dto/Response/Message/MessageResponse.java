package Medco.PremuimFinancing.Dto.Response.Message;

import Medco.PremuimFinancing.Enums.Message.MessageType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MessageResponse {
    private UUID id;
    private MessageType type;
    private String content;
    private Instant createdAt;
    private Instant readAt;
    private String senderUserUuid;
    private String senderName;
    private String receiverUserUuid;
    private String receiverName;

    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String attachmentUrl;
}
