package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.model.StudentWarning;
import com.example.studentanalysissystem.service.impl.RegularScoreWarningServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平时分预警控制器
 */
@RestController
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "平时分预警管理", description = "教师端和学生端的预警功能")
@CrossOrigin(origins = "*")
public class RegularScoreWarningController {

    private final RegularScoreWarningServiceImpl warningService;

    @PostMapping("/generate/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    @Operation(summary = "生成课程预警", description = "为指定课程生成平时分预警")
    public ResponseEntity<?> generateWarnings(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        
        log.info("教师请求为课程{}生成预警", courseId);
        
        try {
            // 从token中获取教师ID（这里需要根据实际的JWT解析逻辑调整）
            Long teacherId = getTeacherIdFromToken(authHeader);
            
            List<Map<String, Object>> warnings = warningService.generateRegularScoreWarnings(courseId, teacherId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "预警生成完成",
                "warningCount", warnings.size(),
                "warnings", warnings
            ));
            
        } catch (Exception e) {
            log.error("生成预警失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/teacher/unprocessed")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "获取教师未处理预警", description = "获取当前教师未处理的预警列表")
    public ResponseEntity<?> getUnprocessedWarnings(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            Long teacherId = getTeacherIdFromToken(authHeader);
            List<StudentWarning> warnings = warningService.getUnprocessedWarnings(teacherId);
            
            // 按课程分组，为每个课程计算一次班级平均分（确保同一课程的平均分一致）
            Map<Long, BigDecimal> courseAverages = new HashMap<>();
            for (StudentWarning warning : warnings) {
                Long courseId = warning.getCourse().getId();
                if (!courseAverages.containsKey(courseId)) {
                    // 动态计算该课程的当前班级平均分
                    BigDecimal currentAverage = warningService.calculateCurrentCourseAverage(courseId);
                    courseAverages.put(courseId, currentAverage);
                }
            }
            
            // 转换为简化的DTO对象，使用动态计算的班级平均分和当前平时分
            List<Map<String, Object>> warningDTOs = warnings.stream()
                .map(warning -> {
                    Long courseId = warning.getCourse().getId();
                    Long studentId = warning.getStudent().getId();
                    
                    // 获取动态计算的班级平均分（同一课程的所有预警使用相同的平均分）
                    BigDecimal dynamicClassAverage = courseAverages.get(courseId);
                    if (dynamicClassAverage == null) {
                        dynamicClassAverage = warning.getClassAverageScore(); // 如果计算失败，使用数据库中的值作为备选
                    }
                    
                    // 动态计算学生的当前平时分
                    BigDecimal currentRegularScore = warningService.calculateCurrentStudentRegularScore(studentId, courseId);
                    if (currentRegularScore == null || currentRegularScore.compareTo(BigDecimal.ZERO) == 0) {
                        currentRegularScore = warning.getCurrentRegularScore(); // 如果计算失败，使用数据库中的值作为备选
                    }
                    
                    return convertToWarningDTO(warning, currentRegularScore, dynamicClassAverage);
                })
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "warnings", warningDTOs
            ));
            
        } catch (Exception e) {
            log.error("获取预警列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * 将 StudentWarning 转换为简化的 DTO 对象（使用动态计算的值）
     */
    private Map<String, Object> convertToWarningDTO(StudentWarning warning, BigDecimal currentRegularScore, BigDecimal classAverageScore) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", warning.getId());
        dto.put("warningLevel", warning.getWarningLevel());
        dto.put("title", warning.getTitle());
        dto.put("content", warning.getContent());
        // 使用动态计算的当前平时分和班级平均分
        dto.put("currentRegularScore", currentRegularScore);
        dto.put("classAverageScore", classAverageScore);
        dto.put("warningThreshold", warning.getWarningThreshold());
        dto.put("isProcessed", warning.getIsProcessed());
        dto.put("createdAt", warning.getCreatedAt());
        dto.put("updatedAt", warning.getUpdatedAt());
        
        // 学生信息
        Map<String, Object> student = new HashMap<>();
        student.put("id", warning.getStudent().getId());
        student.put("name", warning.getStudent().getName());
        student.put("studentNumber", warning.getStudent().getStudentNumber());
        dto.put("student", student);
        
        // 课程信息
        Map<String, Object> course = new HashMap<>();
        course.put("id", warning.getCourse().getId());
        course.put("name", warning.getCourse().getName());
        course.put("code", warning.getCourse().getCode());
        dto.put("course", course);
        
        // 添加 courseId 和 studentId 字段，方便前端过滤
        dto.put("courseId", warning.getCourse().getId());
        dto.put("studentId", warning.getStudent().getId());
        
        return dto;
    }

    @GetMapping("/student/unprocessed")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "获取学生未处理预警", description = "获取当前学生未处理的预警列表")
    public ResponseEntity<?> getStudentUnprocessedWarnings(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long studentId = getStudentIdFromToken(authHeader);
            List<StudentWarning> warnings = warningService.getStudentUnprocessedWarnings(studentId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "warnings", warnings
            ));
            
        } catch (Exception e) {
            log.error("获取学生预警列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/process/{warningId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    @Operation(summary = "处理预警", description = "标记预警为已处理")
    public ResponseEntity<?> processWarning(
            @Parameter(description = "预警ID") @PathVariable Long warningId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String processType = request.get("processType"); // MESSAGE_SENT 或 WARNING_DELETED
            String remark = request.get("remark");
            
            warningService.markWarningAsProcessed(warningId, processType, remark);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "预警处理完成"
            ));
            
        } catch (Exception e) {
            log.error("处理预警失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{warningId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    @Operation(summary = "删除预警", description = "删除指定的预警记录")
    public ResponseEntity<?> deleteWarning(
            @Parameter(description = "预警ID") @PathVariable Long warningId) {
        
        try {
            warningService.deleteWarning(warningId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "预警删除完成"
            ));
            
        } catch (Exception e) {
            log.error("删除预警失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/student/analysis/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "学生成绩分析", description = "分析学生各科成绩，提供改进建议")
    public ResponseEntity<?> analyzeStudentPerformance(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        
        try {
            // 这里需要实现学生成绩分析逻辑
            Map<String, Object> analysis = analyzeStudentScores(studentId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "analysis", analysis
            ));
            
        } catch (Exception e) {
            log.error("分析学生成绩失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 从JWT token中获取教师ID
     */
    private Long getTeacherIdFromToken(String authHeader) {
        try {
            // 如果没有authHeader，返回默认教师ID
            if (authHeader == null || authHeader.trim().isEmpty()) {
                log.info("没有提供Authorization头，使用默认教师ID: 2");
                return 2L;
            }
            
            // 移除 "Bearer " 前缀
            String token = authHeader.replace("Bearer ", "");
            log.info("解析JWT token: {}", token.substring(0, Math.min(50, token.length())) + "...");
            
            // 解析JWT token
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token");
            }
            
            // 解码payload部分
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            log.info("JWT payload: {}", payload);
            
            // 解析JSON获取userId
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(payload);
            
            // 尝试获取userId字段
            if (jsonNode.has("userId")) {
                Long userId = jsonNode.get("userId").asLong();
                log.info("从JWT token中获取到userId: {}", userId);
                return userId;
            } else if (jsonNode.has("teacherId")) {
                Long teacherId = jsonNode.get("teacherId").asLong();
                log.info("从JWT token中获取到teacherId: {}", teacherId);
                return teacherId;
            } else if (jsonNode.has("sub")) {
                String sub = jsonNode.get("sub").asText();
                log.info("从JWT token中获取到sub: {}", sub);
                // 如果sub是邮箱格式，根据邮箱判断用户类型
                if (sub.contains("teacher@example.com")) {
                    return 2L; // teacher1的ID
                } else if (sub.contains("student@example.com")) {
                    return 1L; // student1的ID
                } else {
                    // 尝试解析为数字
                    try {
                        return Long.parseLong(sub);
                    } catch (NumberFormatException e) {
                        log.warn("无法解析sub字段: {}", sub);
                        return 2L; // 默认返回teacher1的ID
                    }
                }
            }
            
            throw new IllegalArgumentException("No valid user ID found in token");
            
        } catch (Exception e) {
            log.error("解析JWT token失败: {}", e.getMessage());
            // 如果解析失败，返回默认值2（teacher1的ID）
            return 2L;
        }
    }

    /**
     * 从JWT token中获取学生ID
     */
    private Long getStudentIdFromToken(String authHeader) {
        // 这里需要根据实际的JWT解析逻辑实现
        // 暂时返回1，实际应该从token中解析
        return 1L;
    }

    /**
     * 分析学生成绩
     */
    private Map<String, Object> analyzeStudentScores(Long studentId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 这里需要实现具体的分析逻辑
        // 分析各科成绩，找出低于80%平均分的科目
        // 提供改进建议
        
        analysis.put("overallAverage", 85.5);
        analysis.put("warningThreshold", 80.0);
        analysis.put("needsImprovement", new String[]{"数学", "物理"});
        analysis.put("suggestions", new String[]{
            "数学：加强基础练习，重点复习微积分",
            "物理：多做实验题，理解物理概念"
        });
        
        return analysis;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取所有预警", description = "获取所有预警数据")
    public ResponseEntity<?> getAllWarnings() {
        try {
            List<Map<String, Object>> warnings = warningService.getAllWarnings();
            return ResponseEntity.ok(warnings);
        } catch (Exception e) {
            log.error("获取所有预警失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取预警列表失败: " + e.getMessage()
            ));
        }
    }
}
