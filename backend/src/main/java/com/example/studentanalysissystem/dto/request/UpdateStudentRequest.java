package com.example.studentanalysissystem.dto.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * 更新学生请求DTO
 */
@Data
public class UpdateStudentRequest {

    private String name;

    private String className;

    private Integer gradeLevel;

    private String major;

    private LocalDate enrollmentDate;

    private LocalDate graduationDate;

    private String remarks;

    private String phone;
}
