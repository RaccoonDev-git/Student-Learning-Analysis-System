package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程权重配置响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseWeightConfigResponse {

    private Long id;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private BigDecimal regularWeight;
    private BigDecimal finalWeight;
    private BigDecimal makeupWeight;
    private Boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
