package com.example.studentanalysissystem.service.ai;

import com.example.studentanalysissystem.dto.request.AIRequest;
import com.example.studentanalysissystem.dto.response.AIResponse;

/**
 * AI模型适配器接口
 * 定义统一的模型调用接口
 */
public interface AIModelAdapter {
    
    /**
     * 模型名称
     */
    String getModelName();
    
    /**
     * 模型描述
     */
    String getModelDescription();
    
    /**
     * 是否可用
     */
    boolean isAvailable();
    
    /**
     * 调用AI模型
     */
    AIResponse call(AIRequest request);
    
    /**
     * 获取模型配置
     */
    ModelConfig getConfig();
    
    /**
     * 设置模型配置
     */
    void setConfig(ModelConfig config);
}
