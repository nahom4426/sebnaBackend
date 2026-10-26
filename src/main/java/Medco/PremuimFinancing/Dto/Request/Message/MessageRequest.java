package Medco.PremuimFinancing.Dto.Request.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    private String receiverUserUuid;
    @NotBlank
    private String content;
}
