package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.AIRequest;
import com.example.studentanalysissystem.dto.response.AIResponse;

/**
 * AI中间件服务接口
 * 统一管理各种大模型的调用
 */
public interface AIMiddlewareService {
    
    /**
     * 分析学生学习情况
     */
        AIResponse analyzeStudentLearning(Long userId, String analysisType);
    
    /**
     * 分析班级整体情况
     */
    AIResponse analyzeClassPerformance(Long teacherId, Long courseId);
    
    /**
     * 生成学习建议
     */
    AIResponse generateStudyAdvice(Long studentId, String context);
    
    /**
     * 通用AI对话
     */
    AIResponse chatWithAI(String message, String context, String userId);
    
    /**
     * 切换AI模型
     */
    boolean switchModel(String modelName);
    
    /**
     * 获取可用模型列表
     */
    java.util.List<String> getAvailableModels();
}
