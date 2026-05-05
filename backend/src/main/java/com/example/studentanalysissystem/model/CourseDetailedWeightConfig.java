package com.example.studentanalysissystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程详细权重配置模型
 * 用于配置每门课程各个成绩类型的详细权重
 */
@Entity
@Table(name = "course_detailed_weight_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CourseDetailedWeightConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "teacher", "enrollments", "grades" })
    private Course course;

    /**
     * 出勤权重（百分比）
     */
    @Column(name = "attendance_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal attendanceWeight = BigDecimal.ZERO;

    /**
     * 作业权重（百分比）
     */
    @Column(name = "homework_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal homeworkWeight = BigDecimal.ZERO;

    /**
     * 实验权重（百分比）
     */
    @Column(name = "lab_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal labWeight = BigDecimal.ZERO;

    /**
     * 随堂测验权重（百分比）
     */
    @Column(name = "quiz_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal quizWeight = BigDecimal.ZERO;

    /**
     * 期中考试权重（百分比）
     */
    @Column(name = "midterm_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal midtermWeight = BigDecimal.ZERO;

    /**
     * 期末考试权重（百分比）
     */
    @Column(name = "final_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal finalWeight = BigDecimal.ZERO;

    /**
     * 补考权重（百分比）
     */
    @Column(name = "makeup_weight", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal makeupWeight = BigDecimal.valueOf(100);

    /**
     * 是否启用详细权重配置
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 配置说明
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void validateWeights() {
        // 计算所有权重总和
        BigDecimal totalWeight = BigDecimal.ZERO;
        if (attendanceWeight != null) totalWeight = totalWeight.add(attendanceWeight);
        if (homeworkWeight != null) totalWeight = totalWeight.add(homeworkWeight);
        if (labWeight != null) totalWeight = totalWeight.add(labWeight);
        if (quizWeight != null) totalWeight = totalWeight.add(quizWeight);
        if (midtermWeight != null) totalWeight = totalWeight.add(midtermWeight);
        if (finalWeight != null) totalWeight = totalWeight.add(finalWeight);

        // 验证权重总和是否为100%
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException("所有成绩类型权重之和必须等于100%");
        }

        // 设置默认补考权重为100%
        if (makeupWeight == null) {
            makeupWeight = BigDecimal.valueOf(100);
        }
    }

    /**
     * 获取平时分总权重（出勤+作业+实验+随堂测验）
     */
    public BigDecimal getRegularTotalWeight() {
        BigDecimal total = BigDecimal.ZERO;
        if (attendanceWeight != null) total = total.add(attendanceWeight);
        if (homeworkWeight != null) total = total.add(homeworkWeight);
        if (labWeight != null) total = total.add(labWeight);
        if (quizWeight != null) total = total.add(quizWeight);
        return total;
    }

    /**
     * 获取考试分总权重（期中+期末）
     */
    public BigDecimal getExamTotalWeight() {
        BigDecimal total = BigDecimal.ZERO;
        if (midtermWeight != null) total = total.add(midtermWeight);
        if (finalWeight != null) total = total.add(finalWeight);
        return total;
    }

    /**
     * 获取所有成绩类型权重总和
     */
    public BigDecimal getTotalWeight() {
        return getRegularTotalWeight().add(getExamTotalWeight());
    }
}
