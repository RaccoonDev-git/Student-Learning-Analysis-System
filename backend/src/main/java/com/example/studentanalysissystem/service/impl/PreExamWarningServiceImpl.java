package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.model.*;
import com.example.studentanalysissystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 考试前预警服务实现类
 * 基于已有成绩预测期末需要达到的分数，提供预警功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PreExamWarningServiceImpl {

    private final CourseDetailedWeightConfigRepository courseDetailedWeightConfigRepository;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * 计算学生期末需要达到的最低分数
     */
    public Map<String, Object> calculateRequiredFinalScore(Long studentId, Long courseId, BigDecimal targetScore) {
        log.info("计算学生{}课程{}期末需要达到的最低分数，目标分数: {}", studentId, courseId, targetScore);

        try {
            // 获取课程权重配置
            CourseDetailedWeightConfig weightConfig = courseDetailedWeightConfigRepository
                    .findByCourseIdAndIsActive(courseId, true)
                    .orElse(null);

            if (weightConfig == null) {
                log.warn("课程{}没有详细权重配置", courseId);
                return createErrorResponse("课程权重配置不存在");
            }

            // 获取学生已有成绩
            List<Grade> existingGrades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
            Map<String, BigDecimal> currentScores = extractCurrentScores(existingGrades);

            // 计算当前平时分
            BigDecimal currentRegularScore = calculateCurrentRegularScore(currentScores, weightConfig);

            // 计算平时分权重和考试分权重
            BigDecimal regularTotalWeight = weightConfig.getRegularTotalWeight();
            BigDecimal examTotalWeight = weightConfig.getExamTotalWeight();

            // 如果考试分权重为0，说明不需要期末考试
            if (examTotalWeight.compareTo(BigDecimal.ZERO) == 0) {
                return createNoExamRequiredResponse(currentRegularScore, targetScore);
            }

            // 计算期末需要达到的最低分数
            BigDecimal requiredFinalScore = calculateRequiredFinalScore(
                    targetScore, currentRegularScore, regularTotalWeight, examTotalWeight);

            // 生成预警信息
            String warningLevel = determineWarningLevel(requiredFinalScore);
            String suggestion = generateSuggestion(requiredFinalScore, currentRegularScore, weightConfig);

            return createSuccessResponse(requiredFinalScore, currentRegularScore, regularTotalWeight, 
                    examTotalWeight, warningLevel, suggestion, currentScores);

        } catch (Exception e) {
            log.error("计算期末需要分数失败: {}", e.getMessage(), e);
            return createErrorResponse("计算失败: " + e.getMessage());
        }
    }

    /**
     * 提取当前已有的成绩
     */
    private Map<String, BigDecimal> extractCurrentScores(List<Grade> grades) {
        Map<String, BigDecimal> scores = new HashMap<>();
        
        for (Grade grade : grades) {
            String examType = grade.getExamType();
            if (scores.containsKey(examType)) {
                // 如果有多个相同类型的成绩，取平均值
                BigDecimal existingScore = scores.get(examType);
                BigDecimal newScore = existingScore.add(grade.getScore()).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                scores.put(examType, newScore);
            } else {
                scores.put(examType, grade.getScore());
            }
        }
        
        return scores;
    }

    /**
     * 计算当前平时分
     */
    private BigDecimal calculateCurrentRegularScore(Map<String, BigDecimal> currentScores, 
                                                   CourseDetailedWeightConfig weightConfig) {
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 计算各种平时分的加权分数
        if (currentScores.containsKey("ATTENDANCE") && weightConfig.getAttendanceWeight() != null) {
            BigDecimal score = currentScores.get("ATTENDANCE");
            BigDecimal weight = weightConfig.getAttendanceWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (currentScores.containsKey("HOMEWORK") && weightConfig.getHomeworkWeight() != null) {
            BigDecimal score = currentScores.get("HOMEWORK");
            BigDecimal weight = weightConfig.getHomeworkWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (currentScores.containsKey("LAB") && weightConfig.getLabWeight() != null) {
            BigDecimal score = currentScores.get("LAB");
            BigDecimal weight = weightConfig.getLabWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (currentScores.containsKey("QUIZ") && weightConfig.getQuizWeight() != null) {
            BigDecimal score = currentScores.get("QUIZ");
            BigDecimal weight = weightConfig.getQuizWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        // 如果权重总和不为100%，按比例调整
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            return totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 计算期末需要达到的最低分数
     */
    private BigDecimal calculateRequiredFinalScore(BigDecimal targetScore, BigDecimal currentRegularScore,
                                                   BigDecimal regularTotalWeight, BigDecimal examTotalWeight) {
        
        // 当前平时分对总分的贡献
        BigDecimal currentRegularContribution = currentRegularScore.multiply(regularTotalWeight)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 期末分需要贡献的分数
        BigDecimal requiredExamContribution = targetScore.subtract(currentRegularContribution);

        // 计算期末需要达到的最低分数
        return requiredExamContribution.multiply(BigDecimal.valueOf(100))
                .divide(examTotalWeight, 2, RoundingMode.HALF_UP);
    }

    /**
     * 确定预警等级
     */
    private String determineWarningLevel(BigDecimal requiredFinalScore) {
        if (requiredFinalScore.compareTo(BigDecimal.valueOf(60)) <= 0) {
            return "SAFE"; // 安全
        } else if (requiredFinalScore.compareTo(BigDecimal.valueOf(80)) <= 0) {
            return "ATTENTION"; // 需要注意
        } else if (requiredFinalScore.compareTo(BigDecimal.valueOf(90)) <= 0) {
            return "WARNING"; // 警告
        } else {
            return "DANGER"; // 危险
        }
    }

    /**
     * 生成学习建议
     */
    private String generateSuggestion(BigDecimal requiredFinalScore, BigDecimal currentRegularScore,
                                     CourseDetailedWeightConfig weightConfig) {
        if (requiredFinalScore.compareTo(BigDecimal.valueOf(60)) <= 0) {
            return "继续保持当前学习状态，期末正常发挥即可达到目标分数。";
        } else if (requiredFinalScore.compareTo(BigDecimal.valueOf(80)) <= 0) {
            return "需要在期末前加强复习，建议制定详细的学习计划，重点攻克薄弱环节。";
        } else if (requiredFinalScore.compareTo(BigDecimal.valueOf(90)) <= 0) {
            return "期末需要非常努力才能达到目标，建议寻求老师或同学的帮助，加强重点内容的学习。";
        } else {
            return "期末需要达到极高分数，建议立即制定紧急学习计划，可能需要考虑补考准备。";
        }
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(BigDecimal requiredFinalScore, BigDecimal currentRegularScore,
                                                      BigDecimal regularTotalWeight, BigDecimal examTotalWeight,
                                                      String warningLevel, String suggestion, Map<String, BigDecimal> currentScores) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("requiredFinalScore", requiredFinalScore);
        response.put("currentRegularScore", currentRegularScore);
        response.put("regularTotalWeight", regularTotalWeight);
        response.put("examTotalWeight", examTotalWeight);
        response.put("warningLevel", warningLevel);
        response.put("suggestion", suggestion);
        response.put("currentScores", currentScores);
        response.put("isAchievable", requiredFinalScore.compareTo(BigDecimal.valueOf(100)) <= 0);
        return response;
    }

    /**
     * 创建无考试需求响应
     */
    private Map<String, Object> createNoExamRequiredResponse(BigDecimal currentRegularScore, BigDecimal targetScore) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("noExamRequired", true);
        response.put("currentRegularScore", currentRegularScore);
        response.put("targetScore", targetScore);
        response.put("isAchievable", currentRegularScore.compareTo(targetScore) >= 0);
        response.put("suggestion", currentRegularScore.compareTo(targetScore) >= 0 ? 
                "恭喜！仅凭平时分就能达到目标分数。" : 
                "平时分不足，需要提高平时表现。");
        return response;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
