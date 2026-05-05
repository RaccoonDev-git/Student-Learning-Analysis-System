package com.example.studentanalysissystem.service.ai;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * AI模型配置类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfig {
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * API端点
     */
    private String apiEndpoint;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 最大Token数
     */
    private Integer maxTokens;
    
    /**
     * 温度参数
     */
    private Double temperature;
    
    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 模型类型 (OPENAI, CLAUDE, LOCAL, CUSTOM)
     */
    private String modelType;
    
    /**
     * 自定义参数
     */
    @Builder.Default
    private Map<String, Object> customParams = new HashMap<>();
    
    /**
     * 获取自定义参数
     */
    public Object getCustomParam(String key) {
        return customParams.get(key);
    }
    
    /**
     * 设置自定义参数
     */
    public void setCustomParam(String key, Object value) {
        customParams.put(key, value);
    }
}
