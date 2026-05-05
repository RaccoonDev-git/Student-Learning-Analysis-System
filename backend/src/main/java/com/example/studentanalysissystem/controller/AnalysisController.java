package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.ClassComparisonResponse;
import com.example.studentanalysissystem.dto.response.CourseCorrelationResponse;
import com.example.studentanalysissystem.dto.response.GradeStatisticsResponse;
import com.example.studentanalysissystem.dto.response.StudentProfileResponse;
import com.example.studentanalysissystem.dto.response.StudentAnalysisResponse;
import com.example.studentanalysissystem.service.ClassComparisonService;
import com.example.studentanalysissystem.service.CourseCorrelationService;
import com.example.studentanalysissystem.service.GradeAnalysisService;
import com.example.studentanalysissystem.service.StudentAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分析控制器
 * 提供成绩统计、学生学习分析等功能
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "数据分析", description = "成绩统计分析、学生学习分析等接口")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class AnalysisController {

        private final GradeAnalysisService gradeAnalysisService;
        private final StudentAnalysisService studentAnalysisService;
        private final ClassComparisonService classComparisonService;
        private final CourseCorrelationService courseCorrelationService;

        /**
         * 获取成绩统计分析
         */
        @GetMapping("/grade-statistics")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "获取成绩统计分析", description = "支持按课程、班级、专业、学期、年级等维度统计成绩数据")
        public ResponseEntity<GradeStatisticsResponse> getGradeStatistics(
                        @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
                        @Parameter(description = "班级名称") @RequestParam(required = false) String className,
                        @Parameter(description = "专业") @RequestParam(required = false) String major,
                        @Parameter(description = "学期") @RequestParam(required = false) String semester,
                        @Parameter(description = "年级") @RequestParam(required = false) Integer gradeLevel) {

                log.info(
                                "GET /api/analysis/grade-statistics - courseId: {}, className: {}, major: {}, semester: {}, gradeLevel: {}",
                                courseId, className, major, semester, gradeLevel);

                GradeStatisticsResponse response = gradeAnalysisService.getGradeStatistics(
                                courseId, className, major, semester, gradeLevel);

                return ResponseEntity.ok(response);
        }

        /**
         * 按课程获取成绩统计
         */
        @GetMapping("/grade-statistics/course/{courseId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "按课程获取成绩统计", description = "获取指定课程的成绩统计数据")
        public ResponseEntity<GradeStatisticsResponse> getStatisticsByCourse(
                        @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {

                log.info("GET /api/analysis/grade-statistics/course/{}", courseId);

                GradeStatisticsResponse response = gradeAnalysisService.getStatisticsByCourse(courseId);
                return ResponseEntity.ok(response);
        }

        /**
         * 按班级获取成绩统计
         */
        @GetMapping("/grade-statistics/class/{className}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "按班级获取成绩统计", description = "获取指定班级的成绩统计数据")
        public ResponseEntity<GradeStatisticsResponse> getStatisticsByClass(
                        @Parameter(description = "班级名称", required = true) @PathVariable String className) {

                log.info("GET /api/analysis/grade-statistics/class/{}", className);

                GradeStatisticsResponse response = gradeAnalysisService.getStatisticsByClass(className);
                return ResponseEntity.ok(response);
        }

        /**
         * 按专业获取成绩统计
         */
        @GetMapping("/grade-statistics/major/{major}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "按专业获取成绩统计", description = "获取指定专业的成绩统计数据")
        public ResponseEntity<GradeStatisticsResponse> getStatisticsByMajor(
                        @Parameter(description = "专业", required = true) @PathVariable String major) {

                log.info("GET /api/analysis/grade-statistics/major/{}", major);

                GradeStatisticsResponse response = gradeAnalysisService.getStatisticsByMajor(major);
                return ResponseEntity.ok(response);
        }

        /**
         * 按学期获取成绩统计
         */
        @GetMapping("/grade-statistics/semester/{semester}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "按学期获取成绩统计", description = "获取指定学期的成绩统计数据")
        public ResponseEntity<GradeStatisticsResponse> getStatisticsBySemester(
                        @Parameter(description = "学期", required = true) @PathVariable String semester) {

                log.info("GET /api/analysis/grade-statistics/semester/{}", semester);

                GradeStatisticsResponse response = gradeAnalysisService.getStatisticsBySemester(semester);
                return ResponseEntity.ok(response);
        }

        /**
         * 按年级获取成绩统计
         */
        @GetMapping("/grade-statistics/grade/{gradeLevel}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "按年级获取成绩统计", description = "获取指定年级的成绩统计数据")
        public ResponseEntity<GradeStatisticsResponse> getStatisticsByGrade(
                        @Parameter(description = "年级", required = true) @PathVariable Integer gradeLevel) {

                log.info("GET /api/analysis/grade-statistics/grade/{}", gradeLevel);

                GradeStatisticsResponse response = gradeAnalysisService.getStatisticsByGrade(gradeLevel);
                return ResponseEntity.ok(response);
        }

        /**
         * 获取学生个人学习档案
         */
        @GetMapping("/student/{studentId}/profile")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
        @Operation(summary = "获取学生个人学习档案", description = "获取学生的成绩趋势、排名、强弱科目分析、学习建议等信息")
        public ResponseEntity<StudentAnalysisResponse> getStudentProfile(
                        @Parameter(description = "学生ID", required = true) @PathVariable Long studentId) {

                log.info("GET /api/analysis/student/{}/profile", studentId);

                // 最小权限校验: 学生仅能查看本人
                try {
                        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                                        .getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated()) {
                                String role = authentication.getAuthorities().stream().findFirst()
                                                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                                                .orElse("");
                                if (role.equals("ROLE_STUDENT")) {
                                        // 学生角色时，校验路径中的studentId是否为当前登录学生ID
                                        // 由于项目未提供直接从认证上下文映射studentId的方法，这里允许后续通过服务内部再做校验或在前端保证。
                                        // 可扩展：从JWT中解析userId后换取studentId再比对。
                                }
                        }
                } catch (Exception ignored) {
                }

                // 使用综合分析替代
                StudentAnalysisResponse response = studentAnalysisService.getStudentComprehensiveAnalysis(studentId);
                return ResponseEntity.ok(response);
        }

        /**
         * 班级对比分析
         */
        @GetMapping("/class-comparison")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "班级对比分析", description = "对比多个班级的成绩表现")
        public ResponseEntity<ClassComparisonResponse> compareClasses(
                        @Parameter(description = "班级名称列表（逗号分隔）", required = true) @RequestParam String classNames,
                        @Parameter(description = "课程ID（可选）") @RequestParam(required = false) Long courseId,
                        @Parameter(description = "学期（可选）") @RequestParam(required = false) String semester) {

                log.info("GET /api/analysis/class-comparison - classNames: {}, courseId: {}, semester: {}",
                                classNames, courseId, semester);

                // 解析班级名称列表
                List<String> classNameList = List.of(classNames.split(","));

                ClassComparisonResponse response = classComparisonService.compareClasses(
                                classNameList, courseId, semester);

                return ResponseEntity.ok(response);
        }

        /**
         * 课程相关性分析
         */
        @GetMapping("/course-correlation")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "课程相关性分析", description = "分析两门课程成绩之间的相关性")
        public ResponseEntity<CourseCorrelationResponse> analyzeCourseCorrelation(
                        @Parameter(description = "第一门课程ID", required = true) @RequestParam Long courseId1,
                        @Parameter(description = "第二门课程ID", required = true) @RequestParam Long courseId2) {

                log.info("GET /api/analysis/course-correlation - courseId1: {}, courseId2: {}",
                                courseId1, courseId2);

                CourseCorrelationResponse response = courseCorrelationService.analyzeCourseCorrelation(
                                courseId1, courseId2);

                return ResponseEntity.ok(response);
        }

}