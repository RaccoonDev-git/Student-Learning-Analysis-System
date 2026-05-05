package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.CreateDetailedWeightConfigRequest;
import com.example.studentanalysissystem.repository.CourseDetailedWeightConfigRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
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
import java.util.Map;

/**
 * 课程详细权重配置控制器
 */
@RestController
@RequestMapping("/api/course-detailed-weight-configs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "课程详细权重配置管理", description = "课程详细权重配置的增删改查")
@CrossOrigin(origins = "*")
public class CourseDetailedWeightConfigController {

    private final CourseDetailedWeightConfigRepository courseDetailedWeightConfigRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取课程详细权重配置", description = "根据课程ID获取详细权重配置")
    public ResponseEntity<?> getByCourseId(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("获取课程{}的详细权重配置", courseId);
        
        Map<String, Object> config = courseDetailedWeightConfigRepository
            .getDetailedWeightConfigByCourseId(courseId);
        
        if (config != null && !config.isEmpty()) {
            return ResponseEntity.ok(config);
        }
        
        // 返回默认配置
        return ResponseEntity.ok(createDefaultConfig(courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    @Operation(summary = "创建或更新详细权重配置", description = "创建或更新课程的详细权重配置")
    public ResponseEntity<?> createOrUpdate(
            @RequestBody CreateDetailedWeightConfigRequest request) {
        
        log.info("保存课程{}的详细权重配置", request.getCourseId());

        // 验证课程是否存在
        int courseCount = courseDetailedWeightConfigRepository.checkCourseExists(request.getCourseId());
        if (courseCount == 0) {
            log.error("课程{}不存在", request.getCourseId());
            return ResponseEntity.badRequest().body(Map.of("error", "课程不存在"));
        }

        // 验证权重总和
        BigDecimal totalWeight = BigDecimal.ZERO;
        if (request.getAttendanceWeight() != null) totalWeight = totalWeight.add(request.getAttendanceWeight());
        if (request.getHomeworkWeight() != null) totalWeight = totalWeight.add(request.getHomeworkWeight());
        if (request.getLabWeight() != null) totalWeight = totalWeight.add(request.getLabWeight());
        if (request.getQuizWeight() != null) totalWeight = totalWeight.add(request.getQuizWeight());
        if (request.getMidtermWeight() != null) totalWeight = totalWeight.add(request.getMidtermWeight());
        if (request.getFinalWeight() != null) totalWeight = totalWeight.add(request.getFinalWeight());

        if (Math.abs(totalWeight.doubleValue() - 100.0) > 0.01) {
            log.error("权重总和不为100%: {}", totalWeight);
            return ResponseEntity.badRequest().body(Map.of("error", "所有成绩类型权重之和必须等于100%"));
        }

        try {
            // 使用原生SQL创建或更新详细权重配置
            int affectedRows = courseDetailedWeightConfigRepository.upsertDetailedWeightConfig(
                    request.getCourseId(),
                    request.getAttendanceWeight() != null ? request.getAttendanceWeight().doubleValue() : 0.0,
                    request.getHomeworkWeight() != null ? request.getHomeworkWeight().doubleValue() : 0.0,
                    request.getLabWeight() != null ? request.getLabWeight().doubleValue() : 0.0,
                    request.getQuizWeight() != null ? request.getQuizWeight().doubleValue() : 0.0,
                    request.getMidtermWeight() != null ? request.getMidtermWeight().doubleValue() : 0.0,
                    request.getFinalWeight() != null ? request.getFinalWeight().doubleValue() : 0.0,
                    request.getMakeupWeight() != null ? request.getMakeupWeight().doubleValue() : 100.0,
                    request.getDescription()
            );

            if (affectedRows > 0) {
                log.info("课程{}的详细权重配置保存成功", request.getCourseId());
                
                // 重新计算该课程的所有综合成绩
                // 注意：综合成绩功能已移除，不再需要重新计算
                log.info("课程{}的权重配置已更新", request.getCourseId());
                
                return ResponseEntity.ok(Map.of("success", true, "message", "详细权重配置保存成功"));
            } else {
                log.error("保存课程{}详细权重配置失败", request.getCourseId());
                return ResponseEntity.badRequest().body(Map.of("error", "保存失败"));
            }
        } catch (Exception e) {
            log.error("保存课程{}详细权重配置时发生异常: {}", request.getCourseId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "保存时发生异常: " + e.getMessage()));
        }
    }

    /**
     * 创建默认配置
     */
    private Map<String, Object> createDefaultConfig(Long courseId) {
        return Map.of(
                "courseId", courseId,
                "attendanceWeight", 20.0,
                "homeworkWeight", 30.0,
                "labWeight", 25.0,
                "quizWeight", 25.0,
                "midtermWeight", 0.0,
                "finalWeight", 0.0,
                "makeupWeight", 100.0,
                "isActive", true,
                "description", "默认配置"
        );
    }
}
