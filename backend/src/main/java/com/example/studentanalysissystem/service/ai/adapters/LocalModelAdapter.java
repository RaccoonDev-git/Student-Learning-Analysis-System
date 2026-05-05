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
 * 本地模型适配器
 * 支持本地部署的大模型（如Ollama、vLLM等）
 */
@Slf4j
@Component
public class LocalModelAdapter implements AIModelAdapter {
    
    private final RestTemplate restTemplate;
    private ModelConfig config;
    
    public LocalModelAdapter() {
        this.restTemplate = new RestTemplate();
        this.config = ModelConfig.builder()
                .modelName("llama2")
                .apiEndpoint("http://localhost:11434/api/generate")
                .maxTokens(2000)
                .temperature(0.7)
                .timeout(60000)
                .enabled(true)
                .modelType("LOCAL")
                .build();
    }
    
    @Override
    public String getModelName() {
        return "Local LLM";
    }
    
    @Override
    public String getModelDescription() {
        return "本地部署的大语言模型，支持Ollama、vLLM等框架";
    }
    
    @Override
    public boolean isAvailable() {
        if (!config.getEnabled()) {
            return false;
        }
        
        // 检查本地服务是否可用
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    config.getApiEndpoint().replace("/api/generate", "/api/tags"), 
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("本地模型服务不可用: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public AIResponse call(AIRequest request) {
        if (!isAvailable()) {
            return AIResponse.builder()
                    .success(false)
                    .error("本地模型服务不可用")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("prompt", request.getSystemPrompt() + "\n\n" + request.getUserPrompt());
            requestBody.put("stream", false);
            requestBody.put("options", Map.of(
                    "temperature", config.getTemperature(),
                    "num_predict", config.getMaxTokens()
            ));
            
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
                if (responseBody != null && responseBody.containsKey("response")) {
                    String content = (String) responseBody.get("response");
                    
                    return AIResponse.builder()
                            .success(true)
                            .content(content)
                            .modelName(getModelName())
                            .timestamp(LocalDateTime.now())
                            .requestId(request.getRequestId())
                            .build();
                }
            }
            
            return AIResponse.builder()
                    .success(false)
                    .error("本地模型API响应格式异常")
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("调用本地模型API失败", e);
            return AIResponse.builder()
                    .success(false)
                    .error("调用本地模型API失败: " + e.getMessage())
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
