package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.SendMessageRequest;
import com.example.studentanalysissystem.dto.response.ChatListItemResponse;
import com.example.studentanalysissystem.dto.response.MessageResponse;
import com.example.studentanalysissystem.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestParam Long senderId,
            @Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/chat-history")
    public ResponseEntity<List<MessageResponse>> getChatHistory(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        List<MessageResponse> messages = messageService.getChatHistory(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取聊天列表
     */
    @GetMapping("/chat-list")
    public ResponseEntity<List<ChatListItemResponse>> getChatList(@RequestParam Long userId) {
        List<ChatListItemResponse> chatList = messageService.getChatList(userId);
        return ResponseEntity.ok(chatList);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        messageService.markMessageAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 标记与某用户的所有消息为已读
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllMessagesAsRead(
            @RequestParam Long userId,
            @RequestParam Long partnerId) {
        messageService.markAllMessagesAsReadBetweenUsers(userId, partnerId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        Long count = messageService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long messageId) {
        MessageResponse message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }
}
