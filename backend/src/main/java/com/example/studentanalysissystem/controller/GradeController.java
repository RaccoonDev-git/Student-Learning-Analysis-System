package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.SubmitGradeRequest;
import com.example.studentanalysissystem.dto.response.GradeResponse;
import com.example.studentanalysissystem.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "成绩管理", description = "成绩提交、查询、统计分析")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @Operation(summary = "提交成绩", description = "教师提交学生成绩")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "提交成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<GradeResponse> submitGrade(@Valid @RequestBody SubmitGradeRequest request) {
        GradeResponse response = gradeService.submitGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询成绩", description = "获取成绩详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "成绩不存在")
    })
    public ResponseEntity<GradeResponse> getGradeById(@PathVariable Long id) {
        GradeResponse response = gradeService.getGradeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学生的所有成绩", description = "获取学生成绩列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getGradesByStudentId(@PathVariable Long studentId) {
        List<GradeResponse> grades = gradeService.getGradesByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程的所有成绩", description = "获取课程成绩列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getGradesByCourseId(@PathVariable Long courseId) {
        List<GradeResponse> grades = gradeService.getGradesByCourseId(courseId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @Operation(summary = "查询学生在某课程的成绩", description = "获取学生在指定课程的成绩")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getGradesByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<GradeResponse> grades = gradeService.getGradesByStudentAndCourse(studentId, courseId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    @Operation(summary = "查询学生某学期的成绩", description = "获取学生在指定学期的成绩单")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getStudentGradesBySemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        List<GradeResponse> grades = gradeService.getStudentGradesBySemester(studentId, semester);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/average")
    @Operation(summary = "计算学生平均分", description = "获取学生的平均成绩")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<BigDecimal> calculateStudentAverageScore(@PathVariable Long studentId) {
        BigDecimal average = gradeService.calculateStudentAverageScore(studentId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/course/{courseId}/average")
    @Operation(summary = "计算课程平均分", description = "获取课程的平均成绩")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<BigDecimal> calculateCourseAverageScore(@PathVariable Long courseId) {
        BigDecimal average = gradeService.calculateCourseAverageScore(courseId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/course/{courseId}/distribution")
    @Operation(summary = "查询课程成绩分布", description = "获取课程成绩的等级分布")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<Map<String, Long>> getCourseGradeDistribution(@PathVariable Long courseId) {
        Map<String, Long> distribution = gradeService.getCourseGradeDistribution(courseId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping
    @Operation(summary = "获取所有成绩", description = "获取所有成绩记录列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        List<GradeResponse> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/failing")
    @Operation(summary = "查询不及格成绩", description = "获取所有不及格成绩列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<GradeResponse>> getFailingGrades() {
        List<GradeResponse> grades = gradeService.getFailingGrades();
        return ResponseEntity.ok(grades);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新成绩", description = "修改学生成绩")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "成绩不存在")
    })
    public ResponseEntity<GradeResponse> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody SubmitGradeRequest request) {
        GradeResponse response = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩", description = "删除成绩记录")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "成绩不存在")
    })
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
