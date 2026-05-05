package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生学习活动统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivityStatsResponse {

    private Long studentId;
    private String studentName;

    // 总体统计
    private Integer totalStudyTime; // 总学习时长(分钟)
    private Long loginCount; // 登录次数
    private Long totalActivities; // 总活动次数

    // 最近活动
    private List<LearningActivityResponse> recentActivities;

    // 每日活动统计
    private List<DailyActivityStat> dailyStats;

    /**
     * 每日活动统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyActivityStat {
        private String date; // 日期 YYYY-MM-DD
        private Long activityCount; // 活动次数
        private Integer studyTime; // 学习时长(分钟)
    }
}
