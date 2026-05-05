package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.EnrollCourseRequest;
import com.example.studentanalysissystem.dto.response.EnrollmentResponse;
import com.example.studentanalysissystem.model.CourseEnrollment;
import com.example.studentanalysissystem.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选课控制器
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "选课管理", description = "学生选课、退课等操作")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "选课", description = "学生选择课程")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "选课成功"),
            @ApiResponse(responseCode = "400", description = "课程已满或已选过该课程")
    })
    public ResponseEntity<EnrollmentResponse> enrollCourse(@Valid @RequestBody EnrollCourseRequest request) {
        EnrollmentResponse response = enrollmentService.enrollCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询选课记录", description = "获取选课详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "选课记录不存在")
    })
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学生的所有选课", description = "获取指定学生的选课列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程的所有选课", description = "获取指定课程的选课列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourseId(@PathVariable Long courseId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    @Operation(summary = "查询学生某学期的选课", description = "获取学生在指定学期的选课列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollmentsBySemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        List<EnrollmentResponse> enrollments = enrollmentService.getStudentEnrollmentsBySemester(studentId, semester);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/check")
    @Operation(summary = "检查是否已选课", description = "查询学生是否已选择某课程")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<Boolean> isStudentEnrolled(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        boolean enrolled = enrollmentService.isStudentEnrolled(studentId, courseId);
        return ResponseEntity.ok(enrolled);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "退课", description = "学生退选课程")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "退课成功"),
            @ApiResponse(responseCode = "404", description = "选课记录不存在")
    })
    public ResponseEntity<EnrollmentResponse> dropCourse(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.dropCourse(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新选课状态", description = "修改选课状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "选课记录不存在")
    })
    public ResponseEntity<EnrollmentResponse> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusStr) {
        CourseEnrollment.EnrollmentStatus status = CourseEnrollment.EnrollmentStatus.valueOf(statusStr.toUpperCase());
        EnrollmentResponse response = enrollmentService.updateEnrollmentStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
