package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.SendMessageRequest;
import com.example.studentanalysissystem.dto.response.ChatListItemResponse;
import com.example.studentanalysissystem.dto.response.MessageResponse;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 发送消息
     */
    MessageResponse sendMessage(Long senderId, SendMessageRequest request);

    /**
     * 获取两个用户之间的聊天记录
     */
    List<MessageResponse> getChatHistory(Long userId1, Long userId2);

    /**
     * 获取用户的聊天列表
     */
    List<ChatListItemResponse> getChatList(Long userId);

    /**
     * 标记消息为已读
     */
    void markMessageAsRead(Long messageId, Long userId);

    /**
     * 标记与某用户的所有消息为已读
     */
    void markAllMessagesAsReadBetweenUsers(Long userId, Long partnerId);

    /**
     * 获取未读消息数量
     */
    Long getUnreadMessageCount(Long userId);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * 获取消息详情
     */
    MessageResponse getMessageById(Long messageId);
}
