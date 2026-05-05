package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.SendMessageRequest;
import com.example.studentanalysissystem.dto.response.ChatListItemResponse;
import com.example.studentanalysissystem.dto.response.MessageResponse;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.model.Message;
import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.repository.MessageRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {
        // 验证发送者
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));

        // 验证接收者
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReceiverId()));

        // 创建消息
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : "text")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        return mapToResponse(savedMessage, sender, receiver);
    }

    @Override
    public List<MessageResponse> getChatHistory(Long userId1, Long userId2) {
        // 验证用户存在
        userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId1));
        userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId2));

        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId1, userId2);

        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatListItemResponse> getChatList(Long userId) {
        // 验证用户存在
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<ChatListItemResponse> chatList = new ArrayList<>();

        // 获取所有其他用户(排除当前用户)
        List<User> allUsers = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());

        for (User partner : allUsers) {
            // 获取最新消息
            Message latestMessage = messageRepository.findLatestMessageBetweenUsers(userId, partner.getId());

            // 获取未读消息数量
            Long unreadCount = messageRepository.countUnreadMessagesBetweenUsers(userId, partner.getId());

            // 创建聊天列表项
            ChatListItemResponse.ChatListItemResponseBuilder builder = ChatListItemResponse.builder()
                    .partnerId(partner.getId())
                    .partnerName(partner.getUsername())
                    .partnerUsername(partner.getUsername())
                    .partnerAvatarUrl(null) // 暂时设为null,后续可添加头像字段
                    .unreadCount(unreadCount);

            if (latestMessage != null) {
                builder.lastMessageContent(latestMessage.getContent())
                        .lastMessageTime(latestMessage.getCreatedAt())
                        .lastMessageFromSelf(latestMessage.getSenderId().equals(userId));
            } else {
                // 如果没有消息记录,设置默认值
                builder.lastMessageContent("还没有消息,开始聊天吧~")
                        .lastMessageTime(LocalDateTime.now())
                        .lastMessageFromSelf(false);
            }

            chatList.add(builder.build());
        }

        // 按最新消息时间倒序排序
        chatList.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));

        return chatList;
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        // 只有接收者可以标记为已读
        if (!message.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("只有接收者可以标记消息为已读");
        }

        if (!message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional
    public void markAllMessagesAsReadBetweenUsers(Long userId, Long partnerId) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, partnerId);

        for (Message message : messages) {
            if (message.getReceiverId().equals(userId) && !message.getIsRead()) {
                message.setIsRead(true);
                message.setReadAt(LocalDateTime.now());
                messageRepository.save(message);
            }
        }
    }

    @Override
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        // 只有发送者可以删除消息
        if (!message.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("只有发送者可以删除消息");
        }

        messageRepository.delete(message);
    }

    @Override
    public MessageResponse getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        return mapToResponse(message);
    }

    // 辅助方法：将Message映射为MessageResponse
    private MessageResponse mapToResponse(Message message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender != null ? sender.getUsername() : "未知用户")
                .receiverId(message.getReceiverId())
                .receiverName(receiver != null ? receiver.getUsername() : "未知用户")
                .content(message.getContent())
                .messageType(message.getMessageType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .build();
    }

    private MessageResponse mapToResponse(Message message, User sender, User receiver) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender.getUsername())
                .receiverId(message.getReceiverId())
                .receiverName(receiver.getUsername())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .build();
    }
}
