package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.LearningActivityResponse;
import com.example.studentanalysissystem.dto.response.StudentActivityStatsResponse;
import com.example.studentanalysissystem.model.LearningActivity.ActivityType;
import com.example.studentanalysissystem.service.LearningActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学习活动控制器
 */
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Learning Activities", description = "学习活动管理接口")
@CrossOrigin(origins = "*")
public class LearningActivityController {

    private final LearningActivityService activityService;

    /**
     * 记录学习活动
     */
    @PostMapping("/record")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "记录学习活动", description = "记录学生的学习活动(登录、查看资料、提交作业等)")
    public ResponseEntity<LearningActivityResponse> recordActivity(
            @RequestBody Map<String, Object> request) {

        Long studentId = ((Number) request.get("studentId")).longValue();
        Long courseId = request.get("courseId") != null
                ? ((Number) request.get("courseId")).longValue()
                : null;
        String activityTypeStr = (String) request.get("activityType");
        ActivityType activityType = ActivityType.valueOf(activityTypeStr);

        @SuppressWarnings("unchecked")
        Map<String, Object> activityData = (Map<String, Object>) request.get("activityData");
        Integer duration = request.get("duration") != null
                ? ((Number) request.get("duration")).intValue()
                : 0;

        var activity = activityService.recordActivity(studentId, courseId,
                activityType, activityData, duration);

        return ResponseEntity.ok(activityService.convertToResponse(activity));
    }

    /**
     * 获取学生活动统计
     */
    @GetMapping("/student/{studentId}/stats")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "获取学生活动统计", description = "获取学生的学习活动统计信息")
    public ResponseEntity<StudentActivityStatsResponse> getStudentStats(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(activityService.getStudentActivityStats(studentId));
    }

    /**
     * 获取学生活动列表
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "获取学生活动列表", description = "获取指定学生的活动记录列表")
    public ResponseEntity<List<LearningActivityResponse>> getStudentActivities(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(activityService.getStudentActivities(studentId, limit));
    }

    /**
     * 获取当前学生自己的活动统计
     */
    @GetMapping("/my-stats")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "获取我的活动统计", description = "学生查看自己的学习活动统计")
    public ResponseEntity<StudentActivityStatsResponse> getMyStats(
            @RequestAttribute("userId") Long userId) {
        // 注意: userId 是通过JWT解析得到的, 需要在SecurityConfig中配置
        // 这里假设已经配置了JWT拦截器并将userId放入request attribute
        return ResponseEntity.ok(activityService.getStudentActivityStats(userId));
    }

    /**
     * 获取课程活动列表
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "获取课程活动列表", description = "获取指定课程的所有学生活动记录")
    public ResponseEntity<List<LearningActivityResponse>> getCourseActivities(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(activityService.getCourseActivities(courseId));
    }

    /**
     * 快捷记录登录
     */
    @PostMapping("/login")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "记录登录", description = "快捷记录学生登录活动")
    public ResponseEntity<String> recordLogin(@RequestAttribute("userId") Long userId) {
        activityService.recordLogin(userId);
        return ResponseEntity.ok("登录记录成功");
    }

    /**
     * 快捷记录查看资料
     */
    @PostMapping("/view-material")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "记录查看资料", description = "快捷记录学生查看学习资料")
    public ResponseEntity<String> recordViewMaterial(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long courseId,
            @RequestParam String materialTitle) {
        activityService.recordViewMaterial(userId, courseId, materialTitle);
        return ResponseEntity.ok("查看资料记录成功");
    }

    /**
     * 快捷记录提交作业
     */
    @PostMapping("/submit-assignment")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "记录提交作业", description = "快捷记录学生提交作业")
    public ResponseEntity<String> recordSubmitAssignment(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long courseId,
            @RequestParam String assignmentTitle) {
        activityService.recordSubmitAssignment(userId, courseId, assignmentTitle);
        return ResponseEntity.ok("提交作业记录成功");
    }
}
