package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.service.AIMiddlewareService;
import com.example.studentanalysissystem.dto.response.AIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI功能控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI智能助手", description = "提供AI驱动的学习分析和资源推荐功能")
public class AIController {
    
    private final AIMiddlewareService aiMiddlewareService;
    
    @PostMapping("/analyze/student/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Operation(summary = "分析学生学习情况", description = "基于学生成绩数据，AI分析学习状况并提供建议")
    public ResponseEntity<AIResponse> analyzeStudentLearning(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "comprehensive") String analysisType,
            @RequestHeader("Authorization") String token) {
        
        AIResponse response = aiMiddlewareService.analyzeStudentLearning(userId, analysisType);
        return ResponseEntity.ok(response);
          }
    
    @PostMapping("/analyze/class/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "分析班级表现", description = "AI分析班级整体学习表现，为教师提供教学建议")
    public ResponseEntity<AIResponse> analyzeClassPerformance(
            @PathVariable Long teacherId,
            @RequestParam Long courseId,
            @RequestHeader("Authorization") String token) {
        
        AIResponse response = aiMiddlewareService.analyzeClassPerformance(teacherId, courseId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/advice/study/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Operation(summary = "生成学习建议", description = "AI根据学生情况生成个性化学习建议")
    public ResponseEntity<AIResponse> generateStudyAdvice(
            @PathVariable Long studentId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        
        String context = request.getOrDefault("context", "");
        AIResponse response = aiMiddlewareService.generateStudyAdvice(studentId, context);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Operation(summary = "AI智能对话", description = "与AI助手进行对话交流")
    public ResponseEntity<AIResponse> chatWithAI(
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        
        String message = request.get("message");
        String context = request.getOrDefault("context", "");
        String userId = request.get("userId");
        
        AIResponse response = aiMiddlewareService.chatWithAI(message, context, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/model/switch")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "切换AI模型", description = "切换当前使用的AI模型")
    public ResponseEntity<Map<String, Object>> switchModel(
            @RequestBody Map<String, String> request) {
        
        String modelName = request.get("modelName");
        boolean success = aiMiddlewareService.switchModel(modelName);
        
        Map<String, Object> response = Map.of(
                "success", success,
                "message", success ? "模型切换成功" : "模型切换失败",
                "currentModel", modelName
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/models")
    @Operation(summary = "获取可用模型", description = "获取系统中所有可用的AI模型列表")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        List<String> models = aiMiddlewareService.getAvailableModels();
        
        Map<String, Object> response = Map.of(
                "available", true,
                "models", models,
                "count", models.size()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    @Operation(summary = "AI服务健康检查", description = "检查AI服务的运行状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        List<String> availableModels = aiMiddlewareService.getAvailableModels();
        boolean isHealthy = !availableModels.isEmpty();
        
        Map<String, Object> response = Map.of(
                "status", isHealthy ? "healthy" : "unhealthy",
                "availableModels", availableModels.size(),
                "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(response);
    }
}
