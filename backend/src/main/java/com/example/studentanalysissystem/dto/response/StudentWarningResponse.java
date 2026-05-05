package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生预警响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentWarningResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String warningType;
    private String warningLevel;
    private String title;
    private String content;
    private BigDecimal currentRegularScore;
    private BigDecimal warningThreshold;
    private Boolean isHandled;
    private LocalDateTime handledAt;
    private Long handledBy;
    private String handledByName;
    private String handleRemarks;
    private String semester;
    private String academicYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}