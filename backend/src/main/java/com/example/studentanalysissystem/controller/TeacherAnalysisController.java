package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.service.TeacherAnalysisService;
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
 * 教师分析功能控制器
 */
@RestController
@RequestMapping("/api/teacher-analysis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "教师分析功能", description = "教师端数据分析功能")
@CrossOrigin(origins = "*")
public class TeacherAnalysisController {

    private final TeacherAnalysisService teacherAnalysisService;

    /**
     * 学生学习轨迹分析
     */
    @GetMapping("/learning-trajectory/{studentId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "学生学习轨迹分析", description = "分析学生在不同课程中的学习表现趋势")
    public ResponseEntity<?> analyzeStudentTrajectory(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "学年") @RequestParam(required = false) String academicYear) {
        
        try {
            Map<String, Object> analysis = teacherAnalysisService.analyzeStudentTrajectory(studentId, courseId, semester, academicYear);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analysis
            ));
        } catch (Exception e) {
            log.error("分析学生学习轨迹失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 课程教学效果分析
     */
    @GetMapping("/course-effectiveness/{courseId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "课程教学效果分析", description = "分析课程的教学效果和学生学习成果")
    public ResponseEntity<?> analyzeCourseEffectiveness(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        
        try {
            Map<String, Object> analysis = teacherAnalysisService.analyzeCourseEffectiveness(courseId, semester, academicYear);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analysis
            ));
        } catch (Exception e) {
            log.error("分析课程教学效果失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 班级学习氛围分析
     */
    @GetMapping("/class-atmosphere/{classId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "班级学习氛围分析", description = "分析班级整体学习氛围和协作情况")
    public ResponseEntity<?> analyzeClassAtmosphere(
            @Parameter(description = "班级ID") @PathVariable String classId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        
        try {
            Map<String, Object> analysis = teacherAnalysisService.analyzeClassAtmosphere(classId, semester, academicYear);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analysis
            ));
        } catch (Exception e) {
            log.error("分析班级学习氛围失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 获取教师所有班级的学习氛围对比
     */
    @GetMapping("/class-comparison/{teacherId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "班级对比分析", description = "对比教师所教班级的学习氛围")
    public ResponseEntity<?> compareClasses(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        
        try {
            Map<String, Object> comparison = teacherAnalysisService.compareClasses(teacherId, semester, academicYear);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comparison
            ));
        } catch (Exception e) {
            log.error("班级对比分析失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

}
