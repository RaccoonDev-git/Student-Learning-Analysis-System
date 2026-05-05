package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID
     */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型: text(文本), image(图片), file(文件)
     */
    @Column(name = "message_type", length = 20)
    private String messageType;

    /**
     * 是否已读
     */
    @Column(name = "is_read")
    private Boolean isRead;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 读取时间
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (messageType == null) {
            messageType = "text";
        }
    }
}
