package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程详细权重配置响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailedWeightConfigResponse {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 出勤权重
     */
    private BigDecimal attendanceWeight;

    /**
     * 作业权重
     */
    private BigDecimal homeworkWeight;

    /**
     * 实验权重
     */
    private BigDecimal labWeight;

    /**
     * 随堂测验权重
     */
    private BigDecimal quizWeight;

    /**
     * 期中考试权重
     */
    private BigDecimal midtermWeight;

    /**
     * 期末考试权重
     */
    private BigDecimal finalWeight;

    /**
     * 补考权重
     */
    private BigDecimal makeupWeight;

    /**
     * 平时分总权重
     */
    private BigDecimal regularTotalWeight;

    /**
     * 考试分总权重
     */
    private BigDecimal examTotalWeight;

    /**
     * 所有成绩类型权重总和
     */
    private BigDecimal totalWeight;

    /**
     * 配置说明
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
