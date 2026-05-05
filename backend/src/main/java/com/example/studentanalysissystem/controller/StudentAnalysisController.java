package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.service.StudentAnalysisService;
import com.example.studentanalysissystem.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生分析控制器
 */
@RestController
@RequestMapping("/api/student-analysis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "学生分析管理", description = "学生端数据分析功能")
@CrossOrigin(origins = "*")
public class StudentAnalysisController {

    private final StudentAnalysisService studentAnalysisService;

    /**
     * 获取学生综合分析数据
     */
    @GetMapping("/comprehensive/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "获取学生综合分析", description = "获取学生的综合学习分析数据")
    public ResponseEntity<?> getStudentComprehensiveAnalysis(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        log.info("获取学生{}的综合分析数据", studentId);
        try {
            StudentAnalysisResponse response = studentAnalysisService.getStudentComprehensiveAnalysis(studentId);
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (Exception e) {
            log.error("获取学生综合分析失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 获取科目专项分析
     */
    @GetMapping("/subject-analysis/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "获取科目专项分析", description = "获取指定科目的专项分析数据")
    public ResponseEntity<?> getSubjectAnalysis(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "科目名称") @RequestParam String subject) {
        log.info("获取学生{}的科目{}专项分析", studentId, subject);
        try {
            SubjectAnalysisResponse response = studentAnalysisService.getSubjectAnalysis(studentId, subject);
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (Exception e) {
            log.error("获取科目分析失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 获取对比分析数据
     */
    @GetMapping("/comparison/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "获取对比分析", description = "获取学生的对比分析数据")
    public ResponseEntity<?> getComparisonAnalysis(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "对比类型") @RequestParam String comparisonType) {
        log.info("获取学生{}的对比分析，类型: {}", studentId, comparisonType);
        try {
            ComparisonAnalysisResponse response = studentAnalysisService.getComparisonAnalysis(
                    studentId, comparisonType);
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (Exception e) {
            log.error("获取对比分析失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 获取学习目标
     */
    @GetMapping("/goals/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "获取学习目标", description = "获取学生的学习目标")
    public ResponseEntity<?> getLearningGoals(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        log.info("获取学生{}的学习目标", studentId);
        try {
            Map<String, Object> response = studentAnalysisService.getLearningGoals(studentId);
            return ResponseEntity.ok(Map.of("success", true, "data", response));
        } catch (Exception e) {
            log.error("获取学习目标失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 设置学习目标
     */
    @PostMapping("/goals/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "设置学习目标", description = "设置学生的学习目标")
    public ResponseEntity<?> setLearningGoals(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestBody Map<String, Object> goals) {
        log.info("设置学生{}的学习目标", studentId);
        try {
            boolean success = studentAnalysisService.setLearningGoals(studentId, goals);
            return ResponseEntity.ok(Map.of("success", success, "message", 
                    success ? "学习目标设置成功" : "学习目标设置失败"));
        } catch (Exception e) {
            log.error("设置学习目标失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

}
