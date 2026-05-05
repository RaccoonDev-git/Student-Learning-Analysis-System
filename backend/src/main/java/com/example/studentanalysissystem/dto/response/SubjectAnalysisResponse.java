package com.example.studentanalysissystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 科目专项分析响应DTO
 */
@Data
@Builder
public class SubjectAnalysisResponse {
    
    private Long studentId;
    private String studentName;
    private String subjectName;
    private String subjectCode;
    
    // 科目成绩统计
    private SubjectScoreStatistics scoreStatistics;
    
    // 优势科目识别
    private List<SubjectStrength> subjectStrengths;
    
    // 薄弱科目分析
    private List<SubjectWeakness> subjectWeaknesses;
    
    // 科目相关性分析
    private List<SubjectCorrelation> subjectCorrelations;
    
    // 学习建议
    private List<String> subjectSuggestions;
    
    // 科目学习计划
    private SubjectStudyPlan studyPlan;
    
    @Data
    @Builder
    public static class SubjectScoreStatistics {
        private BigDecimal averageScore;
        private BigDecimal highestScore;
        private BigDecimal lowestScore;
        private Integer totalGrades;
        private String performanceLevel;
        private String gradeTrend;
        private Map<String, Integer> gradeDistribution;
    }
    
    @Data
    @Builder
    public static class SubjectStrength {
        private String subjectName;
        private BigDecimal averageScore;
        private String strengthType;
        private String description;
        private List<String> strengthAreas;
    }
    
    @Data
    @Builder
    public static class SubjectWeakness {
        private String subjectName;
        private BigDecimal averageScore;
        private String weaknessType;
        private String description;
        private List<String> improvementAreas;
        private List<String> suggestedActions;
    }
    
    @Data
    @Builder
    public static class SubjectCorrelation {
        private String subject1;
        private String subject2;
        private BigDecimal correlationCoefficient;
        private String correlationType;
        private String description;
    }
    
    @Data
    @Builder
    public static class SubjectStudyPlan {
        private String subjectName;
        private List<StudyTask> studyTasks;
        private String studySchedule;
        private List<String> resources;
        private String expectedOutcome;
    }
    
    @Data
    @Builder
    public static class StudyTask {
        private String taskName;
        private String description;
        private String priority;
        private String deadline;
        private String status;
    }
}
