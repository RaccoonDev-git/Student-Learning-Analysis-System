package com.example.studentanalysissystem.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StudentGradeBreakdownResponse {
    
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    
    // 权重配置
    private BigDecimal attendanceWeight;
    private BigDecimal homeworkWeight;
    private BigDecimal labWeight;
    private BigDecimal quizWeight;
    private BigDecimal midtermWeight;
    private boolean isDefaultWeight; // 是否为默认权重
    
    // 各项成绩明细
    private List<GradeDetail> attendanceGrades;
    private List<GradeDetail> homeworkGrades;
    private List<GradeDetail> labGrades;
    private List<GradeDetail> quizGrades;
    private List<GradeDetail> midtermGrades;
    
    // 各项平均分和加权分
    private BigDecimal attendanceAverage;
    private BigDecimal attendanceWeighted;
    private BigDecimal homeworkAverage;
    private BigDecimal homeworkWeighted;
    private BigDecimal labAverage;
    private BigDecimal labWeighted;
    private BigDecimal quizAverage;
    private BigDecimal quizWeighted;
    private BigDecimal midtermAverage;
    private BigDecimal midtermWeighted;
    
    // 总成绩
    private BigDecimal totalScore;
    
    @Data
    @Builder
    public static class GradeDetail {
        private Long gradeId;
        private String examName;
        private BigDecimal score;
        private String examDate;
        private String description;
    }
}
