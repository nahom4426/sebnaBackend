package Medco.PremuimFinancing.Dto.Response.Security;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class LoginLogResponse {
    private UUID id;
    private String email;
    private String userUuid;
    private boolean success;
    private String ipAddress;
    private String userAgent;
    private String failureReason;
    private Instant createdAt;
}
