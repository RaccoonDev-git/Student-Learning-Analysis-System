package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 班级对比分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassComparisonResponse {

    // 班级统计列表
    private List<ClassStats> classStatsList;

    // 总体统计
    private Double overallAverage; // 所有班级的平均分
    private String bestClass; // 成绩最好的班级
    private String worstClass; // 成绩最差的班级
    private Integer totalClasses; // 对比的班级数量

    // 对比时间
    private Date compareDate;

    /**
     * 单个班级的统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassStats {
        private String className; // 班级名称
        private Integer studentCount; // 学生人数
        private Integer gradeCount; // 成绩记录数
        private Double averageScore; // 平均分
        private Double maxScore; // 最高分
        private Double minScore; // 最低分
        private Double passRate; // 及格率（%）
        private Double excellentRate; // 优秀率（%）
        private Double stdDeviation; // 标准差
    }
}