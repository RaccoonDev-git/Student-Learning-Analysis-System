package com.example.studentanalysissystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 创建详细权重配置请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDetailedWeightConfigRequest {

    /**
     * 课程ID
     */
    private Long courseId;

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
     * 配置说明
     */
    private String description;
}
