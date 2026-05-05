package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 成绩统计分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeStatisticsResponse {

    // 统计维度信息
    private String dimension; // 统计维度: course(课程), class(班级), major(专业), semester(学期), grade(年级)
    private String dimensionValue; // 维度值: 如"数据结构"、"计算机1班"等
    private Long dimensionId; // 维度ID(如果适用)

    // 基础统计
    private Integer totalStudents; // 总学生数
    private Double averageScore; // 平均分
    private Double maxScore; // 最高分
    private String maxScoreStudentName; // 最高分学生姓名
    private Double minScore; // 最低分
    private String minScoreStudentName; // 最低分学生姓名
    private Double median; // 中位数
    private Double stdDeviation; // 标准差

    // 率统计
    private Double passRate; // 及格率(>=60)
    private Double excellentRate; // 优秀率(>=90)
    private Double goodRate; // 良好率(>=80 and <90)
    private Double averageRate; // 中等率(>=70 and <80)
    private Double failRate; // 不及格率(<60)

    // 人数统计
    private Integer passCount; // 及格人数
    private Integer excellentCount; // 优秀人数
    private Integer goodCount; // 良好人数
    private Integer averageCount; // 中等人数
    private Integer failCount; // 不及格人数

    // 分数分布(分数段->人数)
    private Map<String, Integer> scoreDistribution;

    // 附加信息
    private String courseName; // 课程名称(如果按课程统计)
    private String className; // 班级名称(如果按班级统计)
    private String major; // 专业(如果按专业统计)
    private String semester; // 学期(如果按学期统计)
    private Integer gradeLevel; // 年级(如果按年级统计)
}