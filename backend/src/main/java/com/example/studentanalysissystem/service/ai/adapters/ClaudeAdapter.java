package com.example.studentanalysissystem.service.ai.adapters;

import com.example.studentanalysissystem.service.ai.AIModelAdapter;
import com.example.studentanalysissystem.service.ai.ModelConfig;
import com.example.studentanalysissystem.dto.request.AIRequest;
import com.example.studentanalysissystem.dto.response.AIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
 * Claude模型适配器
 */
@Slf4j
@Component
public class ClaudeAdapter implements AIModelAdapter {
    
    private final RestTemplate restTemplate;
    private ModelConfig config;
    
    public ClaudeAdapter() {
        this.restTemplate = new RestTemplate();
        this.config = ModelConfig.builder()
                .modelName("claude-3-sonnet-20240229")
                .apiEndpoint("https://api.anthropic.com/v1/messages")
                .maxTokens(2000)
                .temperature(0.7)
                .timeout(30000)
                .enabled(true)
                .modelType("CLAUDE")
                .build();
    }
    
    @Override
    public String getModelName() {
        return "Claude 3 Sonnet";
    }
    
    @Override
    public String getModelDescription() {
        return "Anthropic Claude 3 Sonnet，强大的推理和文本生成能力";
    }
    
    @Override
    public boolean isAvailable() {
        return config.getEnabled() && config.getApiKey() != null && !config.getApiKey().isEmpty();
    }
    
    @Override
    public AIResponse call(AIRequest request) {
        if (!isAvailable()) {
            return AIResponse.builder()
                    .success(false)
                    .error("Claude模型未配置或不可用")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", config.getApiKey());
            headers.set("anthropic-version", "2023-06-01");
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("max_tokens", config.getMaxTokens());
            requestBody.put("temperature", config.getTemperature());
            
            // 构建消息
            String fullPrompt = request.getSystemPrompt() + "\n\n" + request.getUserPrompt();
            requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", fullPrompt)
            });
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    config.getApiEndpoint(),
                    HttpMethod.POST,
                    entity,
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("content")) {
                    Object content = responseBody.get("content");
                    if (content instanceof java.util.List && !((java.util.List<?>) content).isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contentItem = (Map<String, Object>) ((java.util.List<?>) content).get(0);
                        String text = (String) contentItem.get("text");
                        
                        return AIResponse.builder()
                                .success(true)
                                .content(text)
                                .modelName(getModelName())
                                .timestamp(LocalDateTime.now())
                                .requestId(request.getRequestId())
                                .build();
                    }
                }
            }
            
            return AIResponse.builder()
                    .success(false)
                    .error("Claude API响应格式异常")
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("调用Claude API失败", e);
            return AIResponse.builder()
                    .success(false)
                    .error("调用Claude API失败: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
    
    @Override
    public ModelConfig getConfig() {
        return config;
    }
    
    @Override
    public void setConfig(ModelConfig config) {
        this.config = config;
    }
}
