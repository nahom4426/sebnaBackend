package Medco.PremuimFinancing.Controller.Message;

import Medco.PremuimFinancing.Dto.Response.Message.ChatContactResponse;
import Medco.PremuimFinancing.Dto.Response.Message.MessageResponse;
import Medco.PremuimFinancing.Dto.Request.Message.MessageRequest;
import Medco.PremuimFinancing.Entity.Message.Message;
import Medco.PremuimFinancing.Enums.Message.MessageType;
import Medco.PremuimFinancing.Service.Message.MessageService;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_send_message')")
    public MessageResponse send(@Valid @RequestBody MessageRequest request) {
        var auth = SecurityUtils.getAuthenticatedUser();
        Message m = messageService.send(auth.getUserUuid(), request.getReceiverUserUuid(), request.getContent());
        return toResponse(m);
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_send_message')")
    public MessageResponse sendMultipart(
            @RequestParam("receiverUserUuid") String receiverUserUuid,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam("type") String type,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        var auth = SecurityUtils.getAuthenticatedUser();
        MessageType messageType = type == null ? MessageType.TEXT : MessageType.valueOf(type);
        Message m = messageService.send(auth.getUserUuid(), receiverUserUuid, content, messageType, file);
        return toResponse(m);
    }

    @GetMapping("/inbox")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public List<MessageResponse> inbox() {
        var auth = SecurityUtils.getAuthenticatedUser();
        List<Message> messages = messageService.inbox(auth.getUserUuid());
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/contacts")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public List<ChatContactResponse> contacts() {
        var auth = SecurityUtils.getAuthenticatedUser();
        return messageService.contacts(auth.getUserUuid());
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public Map<String, Long> unreadCount() {
        var auth = SecurityUtils.getAuthenticatedUser();
        return Map.of("unread", messageService.unreadCount(auth.getUserUuid()));
    }

    @GetMapping("/unread-by-sender")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public Map<String, Long> unreadBySender() {
        var auth = SecurityUtils.getAuthenticatedUser();
        return messageService.unreadCountBySender(auth.getUserUuid());
    }

    @GetMapping("/thread/{otherUserUuid}")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public Page<MessageResponse> thread(
            @PathVariable String otherUserUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        var auth = SecurityUtils.getAuthenticatedUser();
        return messageService.thread(auth.getUserUuid(), otherUserUuid, PageRequest.of(page, limit))
                .map(this::toResponse);
    }

    @GetMapping("/{messageId}/attachment")
    @PreAuthorize("hasAuthority('ROLE_read_messages')")
    public ResponseEntity<byte[]> attachment(@PathVariable UUID messageId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        Message m = messageService.getAttachmentMessage(auth.getUserUuid(), messageId);
        if (m.getAttachment() == null || m.getAttachment().length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        if (m.getMimeType() != null && !m.getMimeType().isBlank()) {
            headers.setContentType(MediaType.parseMediaType(m.getMimeType()));
        }
        if (m.getFileName() != null && !m.getFileName().isBlank()) {
            headers.setContentDisposition(ContentDisposition.attachment().filename(m.getFileName()).build());
        }
        if (m.getFileSize() != null) {
            headers.setContentLength(m.getFileSize());
        }

        return new ResponseEntity<>(m.getAttachment(), headers, HttpStatus.OK);
    }

    private MessageResponse toResponse(Message m) {
        MessageResponse r = new MessageResponse();
        r.setId(m.getId());
        r.setType(m.getType());
        r.setContent(m.getContent());
        r.setCreatedAt(m.getCreatedAt());
        r.setReadAt(m.getReadAt());
        if (m.getSender() != null) {
            r.setSenderUserUuid(m.getSender().getUserUuid());
            r.setSenderName(m.getSender().getFirstName());
        }
        if (m.getReceiver() != null) {
            r.setReceiverUserUuid(m.getReceiver().getUserUuid());
            r.setReceiverName(m.getReceiver().getFirstName());
        }
        r.setFileName(m.getFileName());
        r.setMimeType(m.getMimeType());
        r.setFileSize(m.getFileSize());
        if (m.getAttachment() != null && m.getAttachment().length > 0) {
            r.setAttachmentUrl("/api/sebna/messages/" + m.getId() + "/attachment");
        }
        return r;
    }
}
