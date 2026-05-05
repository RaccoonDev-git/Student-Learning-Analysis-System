package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选课请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollCourseRequest {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}
