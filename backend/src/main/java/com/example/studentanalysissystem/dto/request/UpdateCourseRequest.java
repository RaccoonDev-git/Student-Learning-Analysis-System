package com.example.studentanalysissystem.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新课程请求DTO
 */
@Data
public class UpdateCourseRequest {

    private String courseName;

    private Integer credits;

    private String description;

    private Integer maxStudents;

    private String classroom;

    private String schedule;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
