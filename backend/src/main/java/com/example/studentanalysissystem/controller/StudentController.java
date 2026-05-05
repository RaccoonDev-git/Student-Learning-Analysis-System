package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.CreateStudentRequest;
import com.example.studentanalysissystem.dto.request.UpdateStudentRequest;
import com.example.studentanalysissystem.dto.response.StudentResponse;
import com.example.studentanalysissystem.service.StudentService;
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
 * 学生控制器
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "学生管理", description = "学生信息的CRUD操作")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "创建学生", description = "添加新学生信息")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或学号已存在")
    })
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询学生", description = "获取学生详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-number/{studentNumber}")
    @Operation(summary = "根据学号查询学生", description = "通过学号获取学生信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<StudentResponse> getStudentByStudentNumber(@PathVariable String studentNumber) {
        StudentResponse response = studentService.getStudentByStudentNumber(studentNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询学生", description = "通过用户ID获取学生信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<StudentResponse> getStudentByUserId(@PathVariable Long userId) {
        StudentResponse response = studentService.getStudentByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "查询所有学生", description = "获取学生列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/class/{className}")
    @Operation(summary = "按班级查询学生", description = "获取指定班级的学生列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> getStudentsByClassName(@PathVariable String className) {
        List<StudentResponse> students = studentService.getStudentsByClassName(className);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/grade/{gradeLevel}")
    @Operation(summary = "按年级查询学生", description = "获取指定年级的学生列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> getStudentsByGradeLevel(@PathVariable Integer gradeLevel) {
        List<StudentResponse> students = studentService.getStudentsByGradeLevel(gradeLevel);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/major/{major}")
    @Operation(summary = "按专业查询学生", description = "获取指定专业的学生列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> getStudentsByMajor(@PathVariable String major) {
        List<StudentResponse> students = studentService.getStudentsByMajor(major);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索学生", description = "根据关键字搜索学生(姓名、学号、班级等)")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> searchStudents(@RequestParam String keyword) {
        List<StudentResponse> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学生信息", description = "修改学生基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生", description = "删除学生信息")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生", description = "根据ID列表批量删除学生")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<Void> batchDeleteStudents(@RequestBody List<Long> ids) {
        studentService.batchDeleteStudents(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "高级筛选学生", description = "根据多个条件筛选学生")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<StudentResponse>> filterStudents(
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String keyword) {
        List<StudentResponse> students = studentService.filterStudents(gradeLevel, className, major, keyword);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/grades")
    @Operation(summary = "获取学生成绩", description = "获取指定学生的所有成绩记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<?> getStudentGrades(@PathVariable Long id) {
        // 这里返回学生成绩信息
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/courses")
    @Operation(summary = "获取学生选课", description = "获取指定学生的选课情况")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "学生不存在")
    })
    public ResponseEntity<?> getStudentCourses(@PathVariable Long id) {
        // 这里返回学生选课信息
        return ResponseEntity.ok().build();
    }

    @GetMapping("/template")
    @Operation(summary = "下载学生导入模板", description = "下载学生信息导入模板文件")
    @ApiResponse(responseCode = "200", description = "下载成功")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            // 创建CSV模板内容
            String csvContent = "姓名,学号,年级,班级,专业,邮箱,电话,入学日期\n" +
                    "张三,20191001,2019,19软工A1,软件工程,zhangsan@example.com,13800138001,2019-09-01\n" +
                    "李四,20191002,2019,19软工A1,软件工程,lisi@example.com,13800138002,2019-09-01\n" +
                    "王五,20191003,2019,19软工A1,软件工程,wangwu@example.com,13800138003,2019-09-01";

            byte[] csvBytes = csvContent.getBytes("UTF-8");

            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("Content-Disposition", "attachment; filename=\"student_import_template.csv\"")
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
