package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.CreateCourseRequest;
import com.example.studentanalysissystem.dto.request.UpdateCourseRequest;
import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.service.CourseService;
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
 * 课程控制器
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程信息的CRUD操作")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "创建课程", description = "添加新课程")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或课程编码已存在")
    })
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询课程", description = "获取课程详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在")
    })
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{courseCode}")
    @Operation(summary = "根据课程编码查询", description = "通过课程编码获取课程信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在")
    })
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String courseCode) {
        CourseResponse response = courseService.getCourseByCode(courseCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "查询所有课程", description = "获取课程列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "按教师查询课程", description = "获取指定教师的所有课程")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getCoursesByTeacherId(@PathVariable Long teacherId) {
        List<CourseResponse> courses = courseService.getCoursesByTeacherId(teacherId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/semester/{semester}")
    @Operation(summary = "按学期查询课程", description = "获取指定学期的课程列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getCoursesBySemester(@PathVariable String semester) {
        List<CourseResponse> courses = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询课程", description = "获取指定状态的课程列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> getCoursesByStatus(@PathVariable String status) {
        Course.CourseStatus courseStatus = Course.CourseStatus.valueOf(status.toUpperCase());
        List<CourseResponse> courses = courseService.getCoursesByStatus(courseStatus);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索课程", description = "根据关键字搜索课程(课程名、编码等)")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public ResponseEntity<List<CourseResponse>> searchCourses(@RequestParam String keyword) {
        List<CourseResponse> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程信息", description = "修改课程基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新课程状态", description = "修改课程状态(ACTIVE, INACTIVE, COMPLETED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在")
    })
    public ResponseEntity<CourseResponse> updateCourseStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusStr) {
        Course.CourseStatus status = Course.CourseStatus.valueOf(statusStr.toUpperCase());
        CourseResponse response = courseService.updateCourseStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "删除课程信息")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "课程不存在")
    })
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
