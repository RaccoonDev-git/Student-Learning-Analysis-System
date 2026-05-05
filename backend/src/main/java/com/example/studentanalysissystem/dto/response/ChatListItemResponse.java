package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天列表项响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatListItemResponse {

    /**
     * 聊天对象用户ID
     */
    private Long partnerId;

    /**
     * 聊天对象姓名
     */
    private String partnerName;

    /**
     * 聊天对象用户名
     */
    private String partnerUsername;

    /**
     * 聊天对象头像URL
     */
    private String partnerAvatarUrl;

    /**
     * 最新消息内容
     */
    private String lastMessageContent;

    /**
     * 最新消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数量
     */
    private Long unreadCount;

    /**
     * 最新消息是否由当前用户发送
     */
    private Boolean lastMessageFromSelf;
}
