package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.model.CourseDetailedWeightConfig;
import com.example.studentanalysissystem.repository.CourseDetailedWeightConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 详细成绩计算服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetailedGradeCalculationServiceImpl {

    private final CourseDetailedWeightConfigRepository courseDetailedWeightConfigRepository;

    /**
     * 使用详细权重配置计算平时分总分
     */
    public BigDecimal calculateDetailedRegularScore(Long courseId, Map<String, BigDecimal> regularScores) {
        if (regularScores == null || regularScores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 获取课程的详细权重配置
        CourseDetailedWeightConfig detailedConfig = courseDetailedWeightConfigRepository
                .findByCourseIdAndIsActive(courseId, true)
                .orElse(null);

        if (detailedConfig == null) {
            log.warn("课程{}没有详细权重配置，使用默认计算方式", courseId);
            return calculateDefaultRegularScore(regularScores);
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 计算各种平时分的加权分数
        if (regularScores.containsKey("ATTENDANCE") && detailedConfig.getAttendanceWeight() != null) {
            BigDecimal score = regularScores.get("ATTENDANCE");
            BigDecimal weight = detailedConfig.getAttendanceWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
            log.debug("出勤分: {} × {}% = {}", score, weight, weightedScore);
        }

        if (regularScores.containsKey("HOMEWORK") && detailedConfig.getHomeworkWeight() != null) {
            BigDecimal score = regularScores.get("HOMEWORK");
            BigDecimal weight = detailedConfig.getHomeworkWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
            log.debug("作业分: {} × {}% = {}", score, weight, weightedScore);
        }

        if (regularScores.containsKey("LAB") && detailedConfig.getLabWeight() != null) {
            BigDecimal score = regularScores.get("LAB");
            BigDecimal weight = detailedConfig.getLabWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
            log.debug("实验分: {} × {}% = {}", score, weight, weightedScore);
        }

        if (regularScores.containsKey("QUIZ") && detailedConfig.getQuizWeight() != null) {
            BigDecimal score = regularScores.get("QUIZ");
            BigDecimal weight = detailedConfig.getQuizWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
            log.debug("测验分: {} × {}% = {}", score, weight, weightedScore);
        }

        // 如果权重总和不为100%，按比例调整
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) != 0 && totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            totalScore = totalScore.multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);
            log.debug("权重归一化: {} ÷ {}% = {}", totalScore.multiply(totalWeight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP), totalWeight, totalScore);
        }

        log.info("课程{}详细平时分计算完成: {}分", courseId, totalScore);
        return totalScore;
    }

    /**
     * 使用详细权重配置计算考试分
     */
    public BigDecimal calculateDetailedExamScore(Long courseId, BigDecimal midtermScore, BigDecimal finalScore) {
        // 获取课程的详细权重配置
        CourseDetailedWeightConfig detailedConfig = courseDetailedWeightConfigRepository
                .findByCourseIdAndIsActive(courseId, true)
                .orElse(null);

        if (detailedConfig == null) {
            log.warn("课程{}没有详细权重配置，使用默认计算方式", courseId);
            return finalScore != null ? finalScore : BigDecimal.ZERO;
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 计算期中考试分
        if (midtermScore != null && detailedConfig.getMidtermWeight() != null && detailedConfig.getMidtermWeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal weightedScore = midtermScore.multiply(detailedConfig.getMidtermWeight())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(detailedConfig.getMidtermWeight());
            log.debug("期中分: {} × {}% = {}", midtermScore, detailedConfig.getMidtermWeight(), weightedScore);
        }

        // 计算期末考试分
        if (finalScore != null && detailedConfig.getFinalWeight() != null && detailedConfig.getFinalWeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal weightedScore = finalScore.multiply(detailedConfig.getFinalWeight())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(detailedConfig.getFinalWeight());
            log.debug("期末分: {} × {}% = {}", finalScore, detailedConfig.getFinalWeight(), weightedScore);
        }

        // 如果权重总和不为100%，按比例调整
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) != 0 && totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            totalScore = totalScore.multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        log.info("课程{}详细考试分计算完成: {}分", courseId, totalScore);
        return totalScore;
    }

    /**
     * 使用详细权重配置计算综合成绩
     */
    public BigDecimal calculateDetailedComprehensiveScore(Long courseId, BigDecimal regularScore, BigDecimal examScore) {
        // 获取课程的详细权重配置
        CourseDetailedWeightConfig detailedConfig = courseDetailedWeightConfigRepository
                .findByCourseIdAndIsActive(courseId, true)
                .orElse(null);

        if (detailedConfig == null) {
            log.warn("课程{}没有详细权重配置，使用默认计算方式", courseId);
            return regularScore != null ? regularScore : BigDecimal.ZERO;
        }

        // 计算平时分和考试分的权重
        BigDecimal regularTotalWeight = detailedConfig.getRegularTotalWeight();
        BigDecimal examTotalWeight = detailedConfig.getExamTotalWeight();
        BigDecimal totalWeight = regularTotalWeight.add(examTotalWeight);

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal regularWeighted = regularScore != null ? 
            regularScore.multiply(regularTotalWeight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal examWeighted = examScore != null ? 
            examScore.multiply(examTotalWeight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal comprehensiveScore = regularWeighted.add(examWeighted);

        log.info("课程{}综合成绩计算完成: 平时分{}×{}% + 考试分{}×{}% = {}", 
                courseId, regularScore, regularTotalWeight, examScore, examTotalWeight, comprehensiveScore);

        return comprehensiveScore;
    }

    /**
     * 默认计算方式（兼容原有逻辑）
     */
    private BigDecimal calculateDefaultRegularScore(Map<String, BigDecimal> regularScores) {
        // 使用固定的默认权重
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        if (regularScores.containsKey("ATTENDANCE")) {
            BigDecimal score = regularScores.get("ATTENDANCE");
            BigDecimal weightedScore = score.multiply(BigDecimal.valueOf(20)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(BigDecimal.valueOf(20));
        }

        if (regularScores.containsKey("HOMEWORK")) {
            BigDecimal score = regularScores.get("HOMEWORK");
            BigDecimal weightedScore = score.multiply(BigDecimal.valueOf(30)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(BigDecimal.valueOf(30));
        }

        if (regularScores.containsKey("LAB")) {
            BigDecimal score = regularScores.get("LAB");
            BigDecimal weightedScore = score.multiply(BigDecimal.valueOf(25)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(BigDecimal.valueOf(25));
        }

        if (regularScores.containsKey("QUIZ")) {
            BigDecimal score = regularScores.get("QUIZ");
            BigDecimal weightedScore = score.multiply(BigDecimal.valueOf(25)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(BigDecimal.valueOf(25));
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            totalScore = totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        return totalScore;
    }
}
