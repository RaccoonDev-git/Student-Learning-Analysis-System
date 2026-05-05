package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.CreateTeacherRequest;
import com.example.studentanalysissystem.dto.request.UpdateTeacherRequest;
import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.dto.response.TeacherResponse;
import com.example.studentanalysissystem.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师信息的CRUD操作")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Operation(summary = "创建教师", description = "添加新教师信息")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或工号已存在")
    })
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        TeacherResponse response = teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询教师", description = "获取教师详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "教师不存在")
    })
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long id) {
        TeacherResponse response = teacherService.getTeacherById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee-number/{employeeNumber}")
    @Operation(summary = "根据工号查询教师", description = "通过工号获取教师信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "教师不存在")
    })
    public ResponseEntity<TeacherResponse> getTeacherByEmployeeNumber(@PathVariable String employeeNumber) {
        TeacherResponse response = teacherService.getTeacherByEmployeeNumber(employeeNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询教师", description = "通过用户ID获取教师信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "教师不存在")
    })
    public ResponseEntity<TeacherResponse> getTeacherByUserId(@PathVariable Long userId) {
        TeacherResponse response = teacherService.getTeacherByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "查询所有教师", description = "获取教师列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        List<TeacherResponse> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "按部门查询教师", description = "获取指定部门的教师列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<TeacherResponse>> getTeachersByDepartment(@PathVariable String department) {
        List<TeacherResponse> teachers = teacherService.getTeachersByDepartment(department);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/title/{title}")
    @Operation(summary = "按职称查询教师", description = "获取指定职称的教师列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<TeacherResponse>> getTeachersByTitle(@PathVariable String title) {
        List<TeacherResponse> teachers = teacherService.getTeachersByTitle(title);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索教师", description = "根据关键字搜索教师(姓名、工号、部门等)")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<TeacherResponse>> searchTeachers(@RequestParam String keyword) {
        List<TeacherResponse> teachers = teacherService.searchTeachers(keyword);
        return ResponseEntity.ok(teachers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教师信息", description = "修改教师基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "教师不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeacherRequest request) {
        TeacherResponse response = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师", description = "删除教师信息")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "教师不存在")
    })
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teacherId}/classes")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "获取教师所教班级", description = "获取指定教师所教授的所有班级")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<String>> getTeacherClasses(@PathVariable Long teacherId) {
        List<String> classes = teacherService.getTeacherClasses(teacherId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{teacherId}/courses")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "获取教师课程", description = "获取指定教师所教授的所有课程")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getTeacherCourses(@PathVariable Long teacherId) {
        List<CourseResponse> courses = teacherService.getTeacherCourses(teacherId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{teacherId}/classes/{className}/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取教师在指定班级的课程", description = "获取指定教师在指定班级所教授的课程")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getTeacherCoursesInClass(
            @PathVariable Long teacherId,
            @PathVariable String className) {
        List<CourseResponse> courses = teacherService.getTeacherCoursesInClass(teacherId, className);
        return ResponseEntity.ok(courses);
    }


    @GetMapping("/{teacherId}/students")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") // 暂时注释掉权限验证
    @Operation(summary = "获取教师管理的学生", description = "获取指定教师所教授班级的所有学生")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<com.example.studentanalysissystem.dto.response.StudentResponse>> getTeacherStudents(@PathVariable Long teacherId) {
        List<com.example.studentanalysissystem.dto.response.StudentResponse> students = teacherService.getTeacherStudents(teacherId);
        return ResponseEntity.ok(students);
    }
}