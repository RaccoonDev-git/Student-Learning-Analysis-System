package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.LoginRequest;
import com.example.studentanalysissystem.dto.request.RegisterRequest;
import com.example.studentanalysissystem.dto.request.UpdateAvatarRequest;
import com.example.studentanalysissystem.dto.request.UpdateUserRequest;
import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或用户名已存在")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名和密码,返回用户信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "403", description = "账号已被禁用")
    })
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户", description = "获取指定用户的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "修改用户基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户账号")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusStr) {
        com.example.studentanalysissystem.model.User.UserStatus status = com.example.studentanalysissystem.model.User.UserStatus
                .valueOf(statusStr.toUpperCase());
        UserResponse response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "400", description = "旧密码错误")
    })
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/avatar")
    @Operation(summary = "更新头像", description = "用户更新自己的头像")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<UserResponse> updateAvatar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvatarRequest request) {
        UserResponse response = userService.updateAvatar(id, request);
        return ResponseEntity.ok(response);
    }
}