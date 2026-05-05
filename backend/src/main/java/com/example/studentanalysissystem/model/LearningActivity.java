package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学习活动实体类
 * 记录学生的各种学习行为
 */
@Entity
@Table(name = "learning_activities", indexes = {
        @Index(name = "idx_student", columnList = "student_id"),
        @Index(name = "idx_course", columnList = "course_id"),
        @Index(name = "idx_type", columnList = "activity_type"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id")
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 50)
    private ActivityType activityType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "activity_data", columnDefinition = "JSON")
    private Map<String, Object> activityData;

    @Column(name = "duration")
    private Integer duration; // 持续时间(分钟)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * 活动类型枚举
     */
    public enum ActivityType {
        LOGIN("登录系统"),
        LOGOUT("退出系统"),
        VIEW_MATERIAL("查看资料"),
        DOWNLOAD_MATERIAL("下载资料"),
        SUBMIT_ASSIGNMENT("提交作业"),
        VIEW_GRADE("查看成绩"),
        TAKE_EXAM("参加考试"),
        TAKE_QUIZ("参加测验"),
        WATCH_VIDEO("观看视频"),
        POST_MESSAGE("发送消息"),
        VIEW_ANNOUNCEMENT("查看公告");

        private final String description;

        ActivityType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
