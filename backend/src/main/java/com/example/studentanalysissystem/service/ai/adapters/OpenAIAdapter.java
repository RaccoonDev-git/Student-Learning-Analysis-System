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
 * OpenAI模型适配器
 */
@Slf4j
@Component
public class OpenAIAdapter implements AIModelAdapter {
    
    private final RestTemplate restTemplate;
    private ModelConfig config;
    
    public OpenAIAdapter() {
        this.restTemplate = new RestTemplate();
        this.config = ModelConfig.builder()
                .modelName("gpt-4")
                .apiEndpoint("https://api.openai.com/v1/chat/completions")
                .maxTokens(2000)
                .temperature(0.7)
                .timeout(30000)
                .enabled(true)
                .modelType("OPENAI")
                .build();
    }
    
    @Override
    public String getModelName() {
        return "OpenAI GPT-4";
    }
    
    @Override
    public String getModelDescription() {
        return "OpenAI GPT-4 大语言模型，适用于各种文本生成和分析任务";
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
                    .error("OpenAI模型未配置或不可用")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", request.getSystemPrompt()),
                Map.of("role", "user", "content", request.getUserPrompt())
            });
            requestBody.put("max_tokens", config.getMaxTokens());
            requestBody.put("temperature", config.getTemperature());
            
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
                if (responseBody != null && responseBody.containsKey("choices")) {
                    Object choices = responseBody.get("choices");
                    if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> choice = (Map<String, Object>) ((java.util.List<?>) choices).get(0);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        String content = (String) message.get("content");
                        
                        return AIResponse.builder()
                                .success(true)
                                .content(content)
                                .modelName(getModelName())
                                .timestamp(LocalDateTime.now())
                                .requestId(request.getRequestId())
                                .build();
                    }
                }
            }
            
            return AIResponse.builder()
                    .success(false)
                    .error("OpenAI API响应格式异常")
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("调用OpenAI API失败", e);
            return AIResponse.builder()
                    .success(false)
                    .error("调用OpenAI API失败: " + e.getMessage())
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
