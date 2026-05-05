package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.StudentGradeBreakdownResponse;
import com.example.studentanalysissystem.service.StudentGradeBreakdownService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.studentanalysissystem.security.JwtUtil;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/student-grade-breakdown")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "学生成绩明细", description = "学生成绩明细查询")
@CrossOrigin(origins = "*")
public class StudentGradeBreakdownController {

    private final StudentGradeBreakdownService studentGradeBreakdownService;
    private final JwtUtil jwtUtil;

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取学生指定课程的成绩明细", description = "根据课程ID获取当前学生的成绩明细")
    public ResponseEntity<StudentGradeBreakdownResponse> getStudentGradeBreakdown(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        
        // 临时使用固定的学生ID进行测试
        Long studentId = 4L;
        
        log.info("获取学生{}在课程{}的成绩明细", studentId, courseId);
        
        StudentGradeBreakdownResponse response = studentGradeBreakdownService
            .getStudentGradeBreakdown(studentId, courseId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    @Operation(summary = "获取学生所有课程的成绩明细", description = "获取当前学生所有课程的成绩明细")
    public ResponseEntity<List<StudentGradeBreakdownResponse>> getAllStudentGradeBreakdowns(
            HttpServletRequest request) {
        
        // 获取当前用户ID
        String token = request.getHeader("Authorization");
        Long studentId = null;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            studentId = jwtUtil.extractUserId(token);
        }
        
        log.info("获取学生{}所有课程的成绩明细", studentId);
        
        List<StudentGradeBreakdownResponse> responses = studentGradeBreakdownService
            .getAllStudentGradeBreakdowns(studentId);
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "获取指定学生指定课程的成绩明细", description = "管理员和教师可查看任意学生的成绩明细")
    public ResponseEntity<StudentGradeBreakdownResponse> getStudentGradeBreakdownByAdmin(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        
        log.info("管理员/教师获取学生{}在课程{}的成绩明细", studentId, courseId);
        
        StudentGradeBreakdownResponse response = studentGradeBreakdownService
            .getStudentGradeBreakdown(studentId, courseId);
        
        return ResponseEntity.ok(response);
    }
}
