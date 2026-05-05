package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建教师请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeacherRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "工号不能为空")
    @Size(max = 50, message = "工号长度不能超过50个字符")
    private String employeeNumber;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    @Size(max = 100, message = "部门长度不能超过100个字符")
    private String department;

    @Size(max = 50, message = "职称长度不能超过50个字符")
    private String title;

    private String remarks;
}
