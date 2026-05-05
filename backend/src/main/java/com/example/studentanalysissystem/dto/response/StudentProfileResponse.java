package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生个人学习档案响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    // 学生基本信息
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private String className;
    private Integer gradeLevel;
    private String major;

    // 总体概览
    private Summary summary;

    // 成绩趋势
    private List<SemesterScore> scoreTrend;

    // 科目分析
    private List<SubjectAnalysis> subjectAnalysis;

    // 学习活动
    private LearningActivity learningActivity;

    // 学习建议
    private List<String> suggestions;

    /**
     * 总体概览
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Double overallAverage; // 总平均分
        private Integer classRank; // 班级排名
        private Integer classSize; // 班级总人数
        private Integer totalCourses; // 总课程数
        private Integer passedCourses; // 及格课程数
        private Integer failedCourses; // 不及格课程数
        private Double gpa; // GPA(平均绩点)
    }

    /**
     * 学期成绩
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterScore {
        private String semester; // 学期
        private Double average; // 平均分
        private Integer courseCount; // 课程数
        private Integer passedCount; // 及格课程数
    }

    /**
     * 科目分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectAnalysis {
        private Long courseId; // 课程ID
        private String courseName; // 课程名称
        private Double score; // 学生成绩
        private Double classAverage; // 班级平均分
        private Integer rank; // 班级排名
        private String trend; // 趋势: improving(上升), stable(稳定), declining(下降)
        private String category; // 分类: strong(强科), average(一般), weak(弱科)
        private Double difference; // 与班级平均分的差值
        private String semester; // 学期
    }

    /**
     * 学习活动
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningActivity {
        private Integer loginCount; // 登录次数
        private Integer totalStudyTime; // 总学习时长(分钟)
        private String activeLevel; // 活跃度: high(高), medium(中), low(低)
        private Integer avgStudyTimePerDay; // 平均每天学习时长
        private String lastLoginDate; // 最后登录日期
    }
}