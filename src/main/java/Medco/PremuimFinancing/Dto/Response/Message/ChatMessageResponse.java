package Medco.PremuimFinancing.Dto.Response.Message;

import Medco.PremuimFinancing.Enums.Message.MessageType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ChatMessageResponse {
    private UUID id;
    private MessageType type;
    private String content;
    private Instant createdAt;
    private String senderUserUuid;
    private String receiverUserUuid;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String attachmentUrl;
}
