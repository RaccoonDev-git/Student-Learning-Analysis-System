package com.example.studentanalysissystem.dto.request;

import com.example.studentanalysissystem.model.Course;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建课程请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseRequest {

    @NotBlank(message = "课程代码不能为空")
    @Size(max = 20, message = "课程代码长度不能超过20个字符")
    private String code;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    private String name;

    private String description;

    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @NotNull(message = "学分不能为空")
    @Min(value = 1, message = "学分必须大于0")
    @Max(value = 10, message = "学分不能超过10")
    private Integer credits;

    @Size(max = 20, message = "学期长度不能超过20个字符")
    private String semester;

    @Size(max = 20, message = "学年长度不能超过20个字符")
    private String academicYear;

    @Min(value = 1, message = "最大学生数必须大于0")
    private Integer maxStudents;

    private Course.CourseStatus status;
}
