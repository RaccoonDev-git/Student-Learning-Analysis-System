package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.StudentAnalysisResponse;
import com.example.studentanalysissystem.dto.response.SubjectAnalysisResponse;
import com.example.studentanalysissystem.dto.response.ComparisonAnalysisResponse;

import java.util.Map;

/**
 * 学生分析服务接口
 */
public interface StudentAnalysisService {

    /**
     * 获取学生综合分析数据
     */
    StudentAnalysisResponse getStudentComprehensiveAnalysis(Long studentId);

    /**
     * 获取科目专项分析
     */
    SubjectAnalysisResponse getSubjectAnalysis(Long studentId, String subject);

    /**
     * 获取对比分析数据
     */
    ComparisonAnalysisResponse getComparisonAnalysis(Long studentId, String comparisonType);

    /**
     * 获取学习目标设定
     */
    Map<String, Object> getLearningGoals(Long studentId);

    /**
     * 设置学习目标
     */
    boolean setLearningGoals(Long studentId, Map<String, Object> goals);

}