package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程相关性分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCorrelationResponse {

    // 课程1信息
    private CourseInfo course1;

    // 课程2信息
    private CourseInfo course2;

    // 相关系数(-1到1之间, 越接近1表示正相关越强)
    private Double correlationCoefficient;

    // 相关性强度描述: strong(强), moderate(中等), weak(弱), none(无相关)
    private String correlationStrength;

    // 散点图数据
    private List<ScatterPoint> scatterData;

    // 样本数量
    private Integer sampleSize;

    /**
     * 课程信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseInfo {
        private Long id;
        private String name;
        private Double averageScore;
    }

    /**
     * 散点图数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScatterPoint {
        private Double course1Score; // 课程1成绩
        private Double course2Score; // 课程2成绩
        private String studentName; // 学生姓名(可选)
    }
}