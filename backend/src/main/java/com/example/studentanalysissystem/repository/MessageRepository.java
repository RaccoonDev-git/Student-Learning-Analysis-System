package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

        /**
         * 查询两个用户之间的所有消息
         */
        @Query("SELECT m FROM Message m WHERE " +
                        "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
                        "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
                        "ORDER BY m.createdAt ASC")
        List<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        /**
         * 查询用户发送的所有消息
         */
        List<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId);

        /**
         * 查询用户接收的所有消息
         */
        List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

        /**
         * 查询用户接收的未读消息
         */
        List<Message> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);

        /**
         * 查询用户未读消息数量
         */
        Long countByReceiverIdAndIsReadFalse(Long receiverId);

        /**
         * 查询用户的所有聊天对象列表
         */
        @Query("SELECT DISTINCT CASE " +
                        "WHEN m.senderId = :userId THEN m.receiverId " +
                        "ELSE m.senderId END " +
                        "FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId")
        List<Long> findChatPartners(@Param("userId") Long userId);

        /**
         * 查询与特定用户的最新一条消息
         */
        @Query("SELECT m FROM Message m WHERE " +
                        "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
                        "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
                        "ORDER BY m.createdAt DESC LIMIT 1")
        Message findLatestMessageBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        /**
         * 查询与特定用户之间的未读消息数量
         */
        @Query("SELECT COUNT(m) FROM Message m WHERE " +
                        "m.senderId = :partnerId AND m.receiverId = :userId AND m.isRead = false")
        Long countUnreadMessagesBetweenUsers(@Param("userId") Long userId, @Param("partnerId") Long partnerId);
}
