package Medco.PremuimFinancing.ServiceImpl.Message;

import Medco.PremuimFinancing.Dto.Response.Message.ChatContactResponse;
import Medco.PremuimFinancing.Entity.Message.Message;
import Medco.PremuimFinancing.Enums.Message.MessageType;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Message.MessageRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Message.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private static final Duration ONLINE_WINDOW = Duration.ofMinutes(2);

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Message send(String senderUserUuid, String receiverUserUuid, String content) {
        UserEntity sender = userRepository.findByUserUuid(senderUserUuid).orElseThrow();
        UserEntity receiver = userRepository.findByUserUuid(receiverUserUuid).orElseThrow();

        Message m = new Message();
        m.setSender(sender);
        m.setReceiver(receiver);
        m.setType(MessageType.TEXT);
        m.setContent(content);
        return messageRepository.save(m);
    }

    @Override
    @Transactional
    public Message send(String senderUserUuid, String receiverUserUuid, String content, MessageType type, MultipartFile attachment) {
        UserEntity sender = userRepository.findByUserUuid(senderUserUuid).orElseThrow();
        UserEntity receiver = userRepository.findByUserUuid(receiverUserUuid).orElseThrow();

        Message m = new Message();
        m.setSender(sender);
        m.setReceiver(receiver);
        m.setType(type == null ? MessageType.TEXT : type);
        m.setContent(content);

        if (attachment != null && !attachment.isEmpty()) {
            try {
                m.setAttachment(attachment.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read attachment", e);
            }
            m.setFileName(attachment.getOriginalFilename());
            m.setMimeType(attachment.getContentType());
            m.setFileSize(attachment.getSize());
        }

        return messageRepository.save(m);
    }

    @Override
    public List<Message> inbox(String userUuid) {
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        return messageRepository.findBySenderOrReceiverOrderByCreatedAtDesc(user, user);
    }

    @Override
    public Page<Message> thread(String userUuid, String otherUserUuid, Pageable pageable) {
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        UserEntity other = userRepository.findByUserUuid(otherUserUuid).orElseThrow();
        Page<Message> page = messageRepository.findThread(user, other, pageable);
        messageRepository.markThreadAsRead(user, other, Instant.now());
        return page;
    }

    @Override
    public long unreadCount(String userUuid) {
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        return messageRepository.countByReceiverAndReadAtIsNull(user);
    }

    @Override
    public Map<String, Long> unreadCountBySender(String userUuid) {
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        List<Object[]> rows = messageRepository.countUnreadBySenderForReceiver(user);
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            String senderUuid = (String) row[0];
            Long count = (Long) row[1];
            map.put(senderUuid, count == null ? 0L : count);
        }
        return map;
    }

    @Override
    public Message getAttachmentMessage(String requesterUserUuid, UUID messageId) {
        UserEntity requester = userRepository.findByUserUuid(requesterUserUuid).orElseThrow();
        Message m = messageRepository.findById(messageId).orElseThrow();

        boolean allowed = (m.getSender() != null && Objects.equals(m.getSender().getId(), requester.getId()))
                || (m.getReceiver() != null && Objects.equals(m.getReceiver().getId(), requester.getId()));
        if (!allowed) {
            throw new RuntimeException("Not allowed");
        }
        return m;
    }

    @Override
    public List<ChatContactResponse> contacts(String userUuid) {
        UserEntity me = userRepository.findByUserUuid(userUuid).orElseThrow();

        Map<String, Long> unreadBySender = unreadCountBySender(userUuid);

        List<UserEntity> users = userRepository.findAll().stream()
                .filter(u -> !Objects.equals(u.getId(), me.getId()))
                .collect(Collectors.toList());

        users.sort(Comparator
                .comparing((UserEntity u) -> messageRepository.findLastMessageAtBetween(me, u), Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed()
                .thenComparing(u -> (u.getFirstName() == null ? "" : u.getFirstName()).toLowerCase()));

        return users.stream().map(u -> toChatContactResponse(me, u, unreadBySender)).collect(Collectors.toList());
    }

    private ChatContactResponse toChatContactResponse(UserEntity me, UserEntity u, Map<String, Long> unreadBySender) {
        ChatContactResponse r = new ChatContactResponse();
        r.setUserUuid(u.getUserUuid());
        String fullName = (u.getFirstName() == null ? "" : u.getFirstName())
                + (u.getFatherName() == null ? "" : " " + u.getFatherName());
        r.setFullName(fullName.trim());
        r.setRoleName(u.getRole() != null ? u.getRole().getRoleName() : null);
        r.setUnreadCount(unreadBySender.getOrDefault(u.getUserUuid(), 0L));
        r.setLastMessageAt(messageRepository.findLastMessageAtBetween(me, u));

        Instant lastActive = u.getLastActiveAt();
        r.setLastActiveAt(lastActive);
        if (lastActive == null) {
            r.setOnline(false);
            r.setLastActiveText(null);
            return r;
        }

        Duration diff = Duration.between(lastActive, Instant.now());
        boolean online = diff.compareTo(ONLINE_WINDOW) <= 0;
        r.setOnline(online);
        if (online) {
            r.setLastActiveText("active now");
        } else {
            long minutes = Math.max(1, diff.toMinutes());
            if (minutes < 60) {
                r.setLastActiveText("active " + minutes + " minutes ago");
            } else {
                long hours = diff.toHours();
                if (hours < 24) {
                    r.setLastActiveText("active " + hours + " hours ago");
                } else {
                    long days = diff.toDays();
                    r.setLastActiveText("active " + days + " days ago");
                }
            }
        }

        return r;
    }
}
