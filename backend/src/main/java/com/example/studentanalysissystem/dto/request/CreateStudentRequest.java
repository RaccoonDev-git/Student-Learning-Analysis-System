package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建学生请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "学号不能为空")
    @Size(max = 50, message = "学号长度不能超过50个字符")
    private String studentNumber;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    @Size(max = 100, message = "班级名称长度不能超过100个字符")
    private String className;

    @Min(value = 1, message = "年级必须大于0")
    @Max(value = 6, message = "年级不能超过6")
    private Integer gradeLevel;

    @Size(max = 100, message = "专业长度不能超过100个字符")
    private String major;

    private String remarks;
}
