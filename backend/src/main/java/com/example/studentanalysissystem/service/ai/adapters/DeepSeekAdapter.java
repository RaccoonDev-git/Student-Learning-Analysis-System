package com.example.studentanalysissystem.service.ai.adapters;

import com.example.studentanalysissystem.service.ai.AIModelAdapter;
import com.example.studentanalysissystem.service.ai.ModelConfig;
import com.example.studentanalysissystem.dto.request.AIRequest;
import com.example.studentanalysissystem.dto.response.AIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 * DeepSeek模型适配器
 */
@Slf4j
@Component
public class DeepSeekAdapter implements AIModelAdapter {
    
    private final RestTemplate restTemplate;
    private ModelConfig config;
    
    @Value("${ai.middleware.models.deepseek.api-key:}")
    private String apiKey;
    
    @Value("${ai.middleware.models.deepseek.name:DeepSeek Chat}")
    private String modelName;
    
    @Value("${ai.middleware.models.deepseek.endpoint:https://api.deepseek.com/v1/chat/completions}")
    private String endpoint;
    
    @Value("${ai.middleware.models.deepseek.max-tokens:1000}")
    private Integer maxTokens;
    
    @Value("${ai.middleware.models.deepseek.temperature:0.3}")
    private Double temperature;
    
    @Value("${ai.middleware.models.deepseek.timeout:15000}")
    private Integer timeout;
    
    @Value("${ai.middleware.models.deepseek.stream:true}")
    private Boolean stream;
    
    @Value("${ai.middleware.models.deepseek.enabled:true}")
    private Boolean enabled;
    
    public DeepSeekAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("=========================================");
        log.info("开始初始化DeepSeek适配器...");
        log.info("读取到的配置值:");
        log.info("- Model Name: {}", modelName);
        log.info("- Endpoint: {}", endpoint);
        log.info("- API Key: {}", apiKey != null && !apiKey.isEmpty() ? "***已设置*** (长度: " + apiKey.length() + ")" : "❌未设置");
        log.info("- Max Tokens: {}", maxTokens);
        log.info("- Temperature: {}", temperature);
        log.info("- Timeout: {}", timeout);
        log.info("- Enabled: {}", enabled);
        
        this.config = ModelConfig.builder()
                .modelName("deepseek-chat")
                .apiEndpoint(endpoint)
                .apiKey(apiKey)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .timeout(timeout)
                .enabled(enabled)
                .modelType("DEEPSEEK")
                .build();
        
        boolean hasApiKey = apiKey != null && !apiKey.isEmpty();
        boolean isEnabled = config.getEnabled();
        
        log.info("DeepSeek适配器初始化完成:");
        log.info("- API Key已设置: {}", hasApiKey ? "✅是" : "❌否");
        log.info("- 模型启用状态: {}", isEnabled ? "✅启用" : "❌禁用");
        log.info("- 适配器可用性: {}", isAvailable() ? "✅可用" : "❌不可用");
        log.info("=========================================");
        
        if (!hasApiKey) {
            log.error("❌❌❌ DeepSeek API Key未设置，请检查配置文件application-ai.yml");
        } else {
            log.info("✅✅✅ DeepSeek适配器配置成功，API Key已加载");
        }
    }
    
    @Override
    public String getModelName() {
        return "DeepSeek Chat";
    }
    
    @Override
    public String getModelDescription() {
        return "DeepSeek Chat 大语言模型，提供强大的推理和文本生成能力，支持中文优化";
    }
    
    @Override
    public boolean isAvailable() {
        return config != null && 
               Boolean.TRUE.equals(config.getEnabled()) && 
               config.getApiKey() != null && 
               !config.getApiKey().isEmpty();
    }
    
    @Override
    public AIResponse call(AIRequest request) {
        if (!isAvailable()) {
            return AIResponse.builder()
                    .success(false)
                    .error("DeepSeek模型未配置或不可用")
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
            // 禁用流式响应，因为 RestTemplate 不支持 SSE 流式响应
            requestBody.put("stream", false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            @SuppressWarnings("unchecked")
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
                        
                        // 提取token使用信息
                        AIResponse.TokenUsage tokenUsage = null;
                        if (responseBody.containsKey("usage")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
                            tokenUsage = AIResponse.TokenUsage.builder()
                                    .promptTokens((Integer) usage.get("prompt_tokens"))
                                    .completionTokens((Integer) usage.get("completion_tokens"))
                                    .totalTokens((Integer) usage.get("total_tokens"))
                                    .build();
                        }
                        
                        return AIResponse.builder()
                                .success(true)
                                .content(content)
                                .modelName(getModelName())
                                .timestamp(LocalDateTime.now())
                                .requestId(request.getRequestId())
                                .tokenUsage(tokenUsage)
                                .build();
                    }
                }
            }
            
            return AIResponse.builder()
                    .success(false)
                    .error("DeepSeek API响应格式异常")
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return AIResponse.builder()
                    .success(false)
                    .error("调用DeepSeek API失败: " + e.getMessage())
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
