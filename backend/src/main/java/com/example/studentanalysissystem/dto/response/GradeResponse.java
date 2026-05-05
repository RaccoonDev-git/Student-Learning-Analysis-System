package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成绩响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String examType;
    private BigDecimal score;
    private BigDecimal totalScore;
    private BigDecimal percentage;
    private String gradeLevel;
    private LocalDate examDate;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
