package com.example.studentanalysissystem.dto.request;

import lombok.Data;

/**
 * 更新教师请求DTO
 */
@Data
public class UpdateTeacherRequest {

    private String department;

    private String title;
}
