package Medco.PremuimFinancing.Dto.Response.Message;

import lombok.Data;

import java.time.Instant;

@Data
public class ChatContactResponse {
    private String userUuid;
    private String fullName;
    private String roleName;
    private Long unreadCount;
    private Instant lastMessageAt;
    private Boolean online;
    private Instant lastActiveAt;
    private String lastActiveText;
}
