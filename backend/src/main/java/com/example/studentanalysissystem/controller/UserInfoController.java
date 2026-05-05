package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.UserInfoResponse;
import com.example.studentanalysissystem.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户信息控制器
 * 提供用户相关的信息查询服务
 */
@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
@Tag(name = "用户信息管理", description = "用户信息查询和管理相关接口")
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 获取所有有效的教师ID列表
     */
    @GetMapping("/valid-teacher-ids")
    @Operation(summary = "获取有效教师ID列表", description = "获取系统中所有有效教师的用户ID列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Long>> getValidTeacherIds() {
        List<Long> teacherIds = userInfoService.getValidTeacherIds();
        return ResponseEntity.ok(teacherIds);
    }

    /**
     * 获取所有有效的学生ID列表
     */
    @GetMapping("/valid-student-ids")
    @Operation(summary = "获取有效学生ID列表", description = "获取系统中所有有效学生的用户ID列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Long>> getValidStudentIds() {
        List<Long> studentIds = userInfoService.getValidStudentIds();
        return ResponseEntity.ok(studentIds);
    }

    /**
     * 获取用户基本信息列表（用于头像加载优化）
     */
    @GetMapping("/basic-info")
    @Operation(summary = "获取用户基本信息", description = "获取所有用户的基本信息，包括ID、姓名、头像等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<UserInfoResponse>> getAllUserBasicInfo() {
        List<UserInfoResponse> userInfoList = userInfoService.getAllUserBasicInfo();
        return ResponseEntity.ok(userInfoList);
    }
}
