package com.example.studentanalysissystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生预警模型
 * 存储教师端的学生预警信息
 */
@Entity
@Table(name = "student_warnings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class StudentWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    /**
     * 预警类型
     * LOW_REGULAR_SCORE: 平时分偏低
     */
    @Column(name = "warning_type", nullable = false, length = 50)
    private String warningType;

    /**
     * 预警等级
     * LIGHT: 轻度提醒（低于平均分但高于60%）
     * SEVERE: 重度提醒（低于60%）
     */
    @Column(name = "warning_level", nullable = false, length = 20)
    private String warningLevel;

    /**
     * 预警标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 预警内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 当前平时分
     */
    @Column(name = "current_regular_score", precision = 5, scale = 2)
    private BigDecimal currentRegularScore;

    /**
     * 班级平均分
     */
    @Column(name = "class_average_score", precision = 5, scale = 2)
    private BigDecimal classAverageScore;

    /**
     * 预警阈值
     */
    @Column(name = "warning_threshold", precision = 5, scale = 2)
    private BigDecimal warningThreshold;

    /**
     * 是否已处理
     */
    @Column(name = "is_processed", nullable = false)
    @Builder.Default
    private Boolean isProcessed = false;

    /**
     * 处理时间
     */
    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    /**
     * 处理人
     */
    @Column(name = "handled_by", length = 100)
    private String handledBy;

    /**
     * 处理备注
     */
    @Column(name = "handle_remarks", columnDefinition = "TEXT")
    private String handleRemarks;

    /**
     * 处理方式
     * MESSAGE_SENT: 已发送消息
     * WARNING_DELETED: 已删除提醒
     */
    @Column(name = "process_type", length = 50)
    private String processType;

    /**
     * 处理备注（兼容旧字段名）
     */
    @Column(name = "process_remark", columnDefinition = "TEXT")
    private String processRemark;

    /**
     * 学期
     */
    @Column(name = "semester", length = 50)
    private String semester;

    /**
     * 学年
     */
    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}