package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.dto.response.GradeResponse;
import com.example.studentanalysissystem.dto.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * AI数据预处理服务
 * 用于优化AI分析的数据准备和缓存
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIDataPreprocessingService {

    private final StudentService studentService;
    private final GradeService gradeService;

    /**
     * 预处理学生分析数据
     */
    @Cacheable(value = "ai-analysis-data", key = "#studentId + '_' + #analysisType")
    public Map<String, Object> preprocessStudentAnalysisData(Long studentId, String analysisType) {
        log.info("预处理学生{}的AI分析数据，类型: {}", studentId, analysisType);
        
        try {
            // 获取学生信息
            StudentResponse studentResponse = studentService.getStudentById(studentId);
            List<GradeResponse> grades = gradeService.getGradesByStudentId(studentId);
            
            // 转换为Student模型
            Student student = new Student();
            student.setId(studentResponse.getId());
            student.setName(studentResponse.getName());
            student.setStudentNumber(studentResponse.getStudentNumber());
            student.setClassName(studentResponse.getClassName());
            student.setMajor(studentResponse.getMajor());
            
            // 数据预处理和统计
            Map<String, Object> processedData = new HashMap<>();
            processedData.put("studentInfo", Map.of(
                "id", student.getId(),
                "name", student.getName(),
                "studentNumber", student.getStudentNumber(),
                "className", student.getClassName(),
                "major", student.getMajor()
            ));
            
            // 成绩统计
            Map<String, Object> gradeStats = calculateGradeStatistics(grades);
            processedData.put("gradeStatistics", gradeStats);
            
            // 课程分析
            Map<String, Object> courseAnalysis = analyzeCourses(grades);
            processedData.put("courseAnalysis", courseAnalysis);
            
            // 学习趋势
            Map<String, Object> learningTrend = analyzeLearningTrend(grades);
            processedData.put("learningTrend", learningTrend);
            
            // 生成简化的提示词数据
            String simplifiedPrompt = generateSimplifiedPrompt(student, gradeStats, courseAnalysis);
            processedData.put("simplifiedPrompt", simplifiedPrompt);
            
            return processedData;
            
        } catch (Exception e) {
            log.error("预处理学生分析数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据预处理失败: " + e.getMessage());
        }
    }

    /**
     * 计算成绩统计
     */
    private Map<String, Object> calculateGradeStatistics(List<GradeResponse> grades) {
        Map<String, Object> stats = new HashMap<>();
        
        if (grades.isEmpty()) {
            stats.put("totalGrades", 0);
            stats.put("averageScore", 0.0);
            stats.put("gradeDistribution", Map.of());
            return stats;
        }
        
        // 基础统计
        double totalScore = grades.stream().mapToDouble(g -> g.getScore().doubleValue()).sum();
        double averageScore = totalScore / grades.size();
        
        // 成绩分布
        Map<String, Long> distribution = grades.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                g -> {
                    double score = g.getScore().doubleValue();
                    if (score >= 90) return "优秀(90-100)";
                    else if (score >= 80) return "良好(80-89)";
                    else if (score >= 70) return "中等(70-79)";
                    else if (score >= 60) return "及格(60-69)";
                    else return "不及格(<60)";
                },
                java.util.stream.Collectors.counting()
            ));
        
        stats.put("totalGrades", grades.size());
        stats.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
        stats.put("maxScore", grades.stream().mapToDouble(g -> g.getScore().doubleValue()).max().orElse(0.0));
        stats.put("minScore", grades.stream().mapToDouble(g -> g.getScore().doubleValue()).min().orElse(0.0));
        stats.put("gradeDistribution", distribution);
        
        return stats;
    }

    /**
     * 分析课程表现
     */
    private Map<String, Object> analyzeCourses(List<GradeResponse> grades) {
        Map<String, Object> courseAnalysis = new HashMap<>();
        
        // 按课程分组
        Map<String, List<GradeResponse>> coursesBySubject = grades.stream()
            .collect(java.util.stream.Collectors.groupingBy(GradeResponse::getCourseName));
        
        Map<String, Object> courseStats = new HashMap<>();
        for (Map.Entry<String, List<GradeResponse>> entry : coursesBySubject.entrySet()) {
            String courseName = entry.getKey();
            List<GradeResponse> courseGrades = entry.getValue();
            
            double avgScore = courseGrades.stream()
                .mapToDouble(g -> g.getScore().doubleValue())
                .average()
                .orElse(0.0);
            
            courseStats.put(courseName, Map.of(
                "averageScore", Math.round(avgScore * 100.0) / 100.0,
                "gradeCount", courseGrades.size(),
                "latestGrade", courseGrades.stream()
                    .max((g1, g2) -> g1.getExamDate().compareTo(g2.getExamDate()))
                    .map(g -> g.getScore().doubleValue())
                    .orElse(0.0)
            ));
        }
        
        courseAnalysis.put("bySubject", courseStats);
        courseAnalysis.put("totalCourses", coursesBySubject.size());
        
        return courseAnalysis;
    }

    /**
     * 分析学习趋势
     */
    private Map<String, Object> analyzeLearningTrend(List<GradeResponse> grades) {
        Map<String, Object> trend = new HashMap<>();
        
        if (grades.size() < 2) {
            trend.put("trend", "数据不足");
            trend.put("direction", "无法判断");
            return trend;
        }
        
        // 按时间排序
        List<GradeResponse> sortedGrades = grades.stream()
            .sorted((g1, g2) -> g1.getExamDate().compareTo(g2.getExamDate()))
            .collect(java.util.stream.Collectors.toList());
        
        // 计算趋势
        double firstHalf = sortedGrades.subList(0, sortedGrades.size() / 2).stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average().orElse(0.0);
        
        double secondHalf = sortedGrades.subList(sortedGrades.size() / 2, sortedGrades.size()).stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average().orElse(0.0);
        
        String direction = secondHalf > firstHalf ? "上升" : 
                          secondHalf < firstHalf ? "下降" : "稳定";
        
        trend.put("trend", direction);
        trend.put("firstHalfAvg", Math.round(firstHalf * 100.0) / 100.0);
        trend.put("secondHalfAvg", Math.round(secondHalf * 100.0) / 100.0);
        
        return trend;
    }

    /**
     * 生成简化的提示词
     */
    private String generateSimplifiedPrompt(Student student, Map<String, Object> gradeStats, Map<String, Object> courseAnalysis) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("学生: ").append(student.getName()).append("\n");
        prompt.append("学号: ").append(student.getStudentNumber()).append("\n");
        prompt.append("班级: ").append(student.getClassName()).append("\n");
        prompt.append("专业: ").append(student.getMajor()).append("\n\n");
        
        prompt.append("成绩概况:\n");
        prompt.append("- 总成绩数: ").append(gradeStats.get("totalGrades")).append("\n");
        prompt.append("- 平均分: ").append(gradeStats.get("averageScore")).append("\n");
        prompt.append("- 最高分: ").append(gradeStats.get("maxScore")).append("\n");
        prompt.append("- 最低分: ").append(gradeStats.get("minScore")).append("\n\n");
        
        prompt.append("成绩分布: ").append(gradeStats.get("gradeDistribution")).append("\n\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> courseStats = (Map<String, Object>) courseAnalysis.get("bySubject");
        if (courseStats != null && !courseStats.isEmpty()) {
            prompt.append("各科表现:\n");
            courseStats.forEach((course, stats) -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> courseData = (Map<String, Object>) stats;
                prompt.append("- ").append(course).append(": 平均")
                     .append(courseData.get("averageScore")).append("分\n");
            });
        }
        
        return prompt.toString();
    }
}
