package com.example.studentanalysissystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 学生综合分析响应DTO
 */
@Data
@Builder
public class StudentAnalysisResponse {
    
    // 基础信息
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private String className;
    private String major;
    
    // 成绩概览
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;
    private Integer totalGrades;
    private Integer totalCourses;
    
    // 成绩分布
    private Map<String, Integer> gradeDistribution;
    
    // 课程成绩对比
    private List<CourseScoreComparison> courseComparisons;
    
    // 成绩趋势
    private List<ScoreTrend> scoreTrends;
    
    // 学习进度
    private LearningProgressSummary learningProgress;
    
    // 学习行为
    private LearningBehaviorSummary learningBehavior;
    
    // 对比分析
    private ComparisonSummary comparison;
    
    // 学习建议
    private List<String> learningSuggestions;
    
    @Data
    @Builder
    public static class CourseScoreComparison {
        private String courseName;
        private BigDecimal averageScore;
        private BigDecimal maxScore;
        private BigDecimal minScore;
        private Integer gradeCount;
        private String performance;
    }
    
    @Data
    @Builder
    public static class ScoreTrend {
        private String timePeriod;
        private BigDecimal averageScore;
        private String trend;
    }
    
    @Data
    @Builder
    public static class LearningProgressSummary {
        private Integer completedCourses;
        private Integer totalCourses;
        private BigDecimal completionRate;
        private String currentSemester;
        private List<String> upcomingCourses;
    }
    
    @Data
    @Builder
    public static class LearningBehaviorSummary {
        private Integer totalActivities;
        private BigDecimal averageActivityScore;
        private String mostActiveSubject;
        private String learningPattern;
    }
    
    @Data
    @Builder
    public static class ComparisonSummary {
        private Integer classRank;
        private Integer majorRank;
        private BigDecimal classAverage;
        private BigDecimal majorAverage;
        private String performanceLevel;
    }
}
