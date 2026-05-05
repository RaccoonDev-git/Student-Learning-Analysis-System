package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.CourseDetailedWeightConfigResponse;
import com.example.studentanalysissystem.model.CourseDetailedWeightConfig;
import com.example.studentanalysissystem.repository.CourseDetailedWeightConfigRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生详细成绩控制器
 */
@RestController
@RequestMapping("/api/student/detailed-grades")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "学生详细成绩管理", description = "学生查看详细成绩和权重配置")
@CrossOrigin(origins = "*")
public class StudentDetailedGradeController {

    private final CourseDetailedWeightConfigRepository courseDetailedWeightConfigRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/course/{courseId}/weight-config")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "获取课程详细权重配置", description = "学生查看课程的详细权重配置")
    public ResponseEntity<?> getCourseWeightConfig(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("学生获取课程{}的详细权重配置", courseId);
        
        // 使用新的Repository方法获取可修改的Map
        Map<String, Object> config = courseDetailedWeightConfigRepository
            .getDetailedWeightConfigByCourseId(courseId);
        
        if (config != null && !config.isEmpty()) {
            // 获取课程名称并添加到结果中
            try {
                String courseName = courseRepository.findById(courseId)
                    .map(course -> course.getName())
                    .orElse("未知课程");
                config.put("courseName", courseName);
            } catch (Exception e) {
                log.warn("获取课程名称失败: {}", e.getMessage());
                config.put("courseName", "未知课程");
            }
            
            return ResponseEntity.ok(config);
        }
        
        // 如果数据库查询失败或没有数据，返回默认配置
        Map<String, Object> defaultConfig = createDefaultConfig(courseId);
        
        // 获取课程名称
        try {
            String courseName = courseRepository.findById(courseId)
                .map(course -> course.getName())
                .orElse("未知课程");
            defaultConfig.put("courseName", courseName);
        } catch (Exception e) {
            log.warn("获取课程名称失败: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(defaultConfig);
    }

    @GetMapping("/course/{courseId}/grade-breakdown")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "获取课程成绩明细", description = "学生查看课程的成绩明细和权重说明")
    public ResponseEntity<?> getCourseGradeBreakdown(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("学生获取课程{}的成绩明细", courseId);
        
        try {
            // 获取权重配置
            Map<String, Object> weightConfig = courseDetailedWeightConfigRepository
                .getDetailedWeightConfigByCourseId(courseId);
            
            if (weightConfig == null || weightConfig.isEmpty()) {
                weightConfig = createDefaultConfig(courseId);
            }
            
            // 获取课程名称
            String courseName = courseRepository.findById(courseId)
                .map(course -> course.getName())
                .orElse("未知课程");
            
            // 构建成绩明细响应
            Map<String, Object> breakdown = Map.of(
                "courseId", courseId,
                "courseName", courseName,
                "weightConfig", weightConfig,
                "gradeTypes", Map.of(
                    "regular", Map.of(
                        "attendance", Map.of("name", "出勤", "description", "课堂出勤情况"),
                        "homework", Map.of("name", "作业", "description", "平时作业完成情况"),
                        "lab", Map.of("name", "实验", "description", "实验报告和实验表现"),
                        "quiz", Map.of("name", "随堂测验", "description", "课堂小测验成绩")
                    ),
                    "exam", Map.of(
                        "midterm", Map.of("name", "期中考试", "description", "期中考试成绩"),
                        "final", Map.of("name", "期末考试", "description", "期末考试成绩")
                    )
                )
            );
            
            return ResponseEntity.ok(breakdown);
        } catch (Exception e) {
            log.error("获取课程{}成绩明细失败: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "获取成绩明细失败"));
        }
    }


    /**
     * 创建默认配置
     */
    private Map<String, Object> createDefaultConfig(Long courseId) {
        Map<String, Object> config = new HashMap<>();
        config.put("courseId", courseId);
        config.put("courseName", "未知课程");
        config.put("attendanceWeight", 20.0);
        config.put("homeworkWeight", 30.0);
        config.put("labWeight", 25.0);
        config.put("quizWeight", 25.0);
        config.put("midtermWeight", 0.0);
        config.put("finalWeight", 0.0);
        config.put("makeupWeight", 100.0);
        config.put("isActive", true);
        config.put("description", "默认配置");
        return config;
    }
}
