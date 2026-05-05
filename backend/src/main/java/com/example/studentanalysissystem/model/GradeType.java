package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩类型模型
 * 定义平时分的各种类型（签到、作业、实验、随堂测等）
 */
@Entity
@Table(name = "grade_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 成绩类型代码（如：ATTENDANCE, HOMEWORK, LAB, QUIZ, FINAL, MAKEUP）
     */
    @Column(name = "type_code", nullable = false, length = 50, unique = true)
    private String typeCode;

    /**
     * 成绩类型名称（如：签到、平时作业、实验、随堂测试、期末考试、补考）
     */
    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    /**
     * 是否为平时分类型
     */
    @Column(name = "is_regular", nullable = false)
    @Builder.Default
    private Boolean isRegular = true;

    /**
     * 是否为期末分类型
     */
    @Column(name = "is_final", nullable = false)
    @Builder.Default
    private Boolean isFinal = false;

    /**
     * 是否为补考类型
     */
    @Column(name = "is_makeup", nullable = false)
    @Builder.Default
    private Boolean isMakeup = false;

    /**
     * 默认权重（在平时分中的权重，如签到占平时分的20%）
     */
    @Column(name = "default_weight", precision = 5, scale = 2)
    private BigDecimal defaultWeight;

    /**
     * 满分
     */
    @Column(name = "full_score", precision = 5, scale = 2)
    private BigDecimal fullScore;

    /**
     * 是否启用
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
