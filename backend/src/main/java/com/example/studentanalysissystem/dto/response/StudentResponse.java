package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String studentNumber;
    private String name;
    private String className;
    private Integer gradeLevel;
    private String major;
    private LocalDate enrollmentDate;
    private LocalDate graduationDate;
    private String avatarUrl;
    private Boolean hasCustomAvatar;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
