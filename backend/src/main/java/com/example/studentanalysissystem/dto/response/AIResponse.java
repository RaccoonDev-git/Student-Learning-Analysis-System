package com.example.studentanalysissystem.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * AI响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 使用的模型名称
     */
    private String modelName;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 响应时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 处理耗时（毫秒）
     */
    private Long processingTime;
    
    /**
     * Token使用情况
     */
    private TokenUsage tokenUsage;
    
    /**
     * 扩展数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    /**
     * Token使用情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
    
    /**
     * 获取元数据
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * 设置元数据
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
}
