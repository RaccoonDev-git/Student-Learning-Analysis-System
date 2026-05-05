package com.example.studentanalysissystem.dto.response;

import com.example.studentanalysissystem.model.CourseEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 选课响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDateTime enrollmentDate;
    private CourseEnrollment.EnrollmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
