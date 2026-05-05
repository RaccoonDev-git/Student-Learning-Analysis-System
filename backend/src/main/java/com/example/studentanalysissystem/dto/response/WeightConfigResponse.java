package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 权重配置响应DTO（简化版）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightConfigResponse {

    private Long id;
    private Long courseId;
    private String courseName;
    private BigDecimal attendanceWeight;
    private BigDecimal homeworkWeight;
    private BigDecimal labWeight;
    private BigDecimal quizWeight;
    private BigDecimal midtermWeight;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDefault; // 是否为默认配置
}
