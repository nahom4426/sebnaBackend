package Medco.PremuimFinancing.Service.Message;

import Medco.PremuimFinancing.Dto.Response.Message.ChatContactResponse;
import Medco.PremuimFinancing.Entity.Message.Message;
import Medco.PremuimFinancing.Enums.Message.MessageType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageService {
    Message send(String senderUserUuid, String receiverUserUuid, String content);
    Message send(String senderUserUuid, String receiverUserUuid, String content, MessageType type, MultipartFile attachment);
    List<Message> inbox(String userUuid);

    Page<Message> thread(String userUuid, String otherUserUuid, Pageable pageable);
    Message getAttachmentMessage(String requesterUserUuid, UUID messageId);

    List<ChatContactResponse> contacts(String userUuid);

    long unreadCount(String userUuid);

    Map<String, Long> unreadCountBySender(String userUuid);
}
