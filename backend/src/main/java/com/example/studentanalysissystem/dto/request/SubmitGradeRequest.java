package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 提交成绩请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitGradeRequest {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Size(max = 20, message = "考试类型长度不能超过20个字符")
    private String examType;

    @NotNull(message = "分数不能为空")
    @DecimalMin(value = "0.00", message = "分数不能小于0")
    @DecimalMax(value = "100.00", message = "分数不能大于100")
    private BigDecimal score;

    @NotNull(message = "总分不能为空")
    @DecimalMin(value = "0.01", message = "总分必须大于0")
    private BigDecimal totalScore;

    private LocalDate examDate;

    private String remarks;
}
