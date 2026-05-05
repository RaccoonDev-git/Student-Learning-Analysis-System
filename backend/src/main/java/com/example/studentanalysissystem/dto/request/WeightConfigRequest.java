package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 权重配置请求DTO（简化版，只有5个权重类型）
 */
@Data
public class WeightConfigRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "课堂表现权重不能为空")
    @DecimalMin(value = "0.0", message = "课堂表现权重不能小于0")
    @DecimalMax(value = "100.0", message = "课堂表现权重不能大于100")
    private BigDecimal attendanceWeight;

    @NotNull(message = "作业权重不能为空")
    @DecimalMin(value = "0.0", message = "作业权重不能小于0")
    @DecimalMax(value = "100.0", message = "作业权重不能大于100")
    private BigDecimal homeworkWeight;

    @NotNull(message = "实验权重不能为空")
    @DecimalMin(value = "0.0", message = "实验权重不能小于0")
    @DecimalMax(value = "100.0", message = "实验权重不能大于100")
    private BigDecimal labWeight;

    @NotNull(message = "小测验权重不能为空")
    @DecimalMin(value = "0.0", message = "小测验权重不能小于0")
    @DecimalMax(value = "100.0", message = "小测验权重不能大于100")
    private BigDecimal quizWeight;

    @NotNull(message = "期中考试权重不能为空")
    @DecimalMin(value = "0.0", message = "期中考试权重不能小于0")
    @DecimalMax(value = "100.0", message = "期中考试权重不能大于100")
    private BigDecimal midtermWeight;

    private String description;

    /**
     * 验证权重总和是否为100
     */
    public boolean isValidWeightSum() {
        BigDecimal total = attendanceWeight.add(homeworkWeight).add(labWeight)
                .add(quizWeight).add(midtermWeight);
        return total.compareTo(BigDecimal.valueOf(100.0)) == 0;
    }
}
