package com.example.studentanalysissystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 对比分析响应DTO
 */
@Data
@Builder
public class ComparisonAnalysisResponse {
    
    private Long studentId;
    private String studentName;
    private String comparisonType;
    
    // 班级排名
    private ClassRanking classRanking;
    
    // 专业排名
    private MajorRanking majorRanking;
    
    // 历史对比
    private HistoricalComparison historicalComparison;
    
    // 目标达成度
    private GoalAchievement goalAchievement;
    
    // 对比分析建议
    private List<String> comparisonSuggestions;
    
    @Data
    @Builder
    public static class ClassRanking {
        private Integer currentRank;
        private Integer totalStudents;
        private BigDecimal percentile;
        private String rankingLevel;
        private BigDecimal classAverage;
        private BigDecimal studentAverage;
        private BigDecimal difference;
        private List<RankingChange> rankingHistory;
    }
    
    @Data
    @Builder
    public static class MajorRanking {
        private Integer currentRank;
        private Integer totalStudents;
        private BigDecimal percentile;
        private String rankingLevel;
        private BigDecimal majorAverage;
        private BigDecimal studentAverage;
        private BigDecimal difference;
        private List<RankingChange> rankingHistory;
    }
    
    @Data
    @Builder
    public static class HistoricalComparison {
        private List<PeriodComparison> periodComparisons;
        private String overallTrend;
        private BigDecimal totalImprovement;
        private List<String> improvementAreas;
        private List<String> declineAreas;
    }
    
    @Data
    @Builder
    public static class PeriodComparison {
        private String period;
        private BigDecimal previousScore;
        private BigDecimal currentScore;
        private BigDecimal improvement;
        private String trend;
        private String performance;
    }
    
    @Data
    @Builder
    public static class GoalAchievement {
        private List<Goal> goals;
        private BigDecimal overallAchievementRate;
        private List<String> achievedGoals;
        private List<String> pendingGoals;
        private List<String> exceededGoals;
    }
    
    @Data
    @Builder
    public static class Goal {
        private String goalType;
        private String description;
        private BigDecimal targetValue;
        private BigDecimal currentValue;
        private BigDecimal achievementRate;
        private String status;
        private String deadline;
    }
    
    @Data
    @Builder
    public static class RankingChange {
        private String period;
        private Integer previousRank;
        private Integer currentRank;
        private Integer change;
        private String changeType;
    }
}
