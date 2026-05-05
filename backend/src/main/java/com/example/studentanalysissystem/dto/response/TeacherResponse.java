package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教师响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponse {

    private Long id;
    private Long userId;
    private String username;
    private String employeeNumber;
    private String name;
    private String department;
    private String title;
    private String avatarUrl;
    private Boolean hasCustomAvatar;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
