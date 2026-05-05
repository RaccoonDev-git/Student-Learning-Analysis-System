package com.example.studentanalysissystem.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * AI请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户角色
     */
    private String userRole;
    
    /**
     * 系统提示词
     */
    private String systemPrompt;
    
    /**
     * 用户提示词
     */
    private String userPrompt;
    
    /**
     * 请求类型
     */
    private String requestType;
    
    /**
     * 上下文数据
     */
    @Builder.Default
    private Map<String, Object> context = new HashMap<>();
    
    /**
     * 模型名称（可选，不指定则使用默认模型）
     */
    private String modelName;
    
    /**
     * 最大Token数
     */
    private Integer maxTokens;
    
    /**
     * 温度参数
     */
    private Double temperature;
    
    /**
     * 请求时间
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 获取上下文数据
     */
    public Object getContext(String key) {
        return context.get(key);
    }
    
    /**
     * 设置上下文数据
     */
    public void setContext(String key, Object value) {
        context.put(key, value);
    }
}
