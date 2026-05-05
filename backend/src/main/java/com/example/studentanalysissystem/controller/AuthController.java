package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.LoginRequest;
import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.service.UserService;
import com.example.studentanalysissystem.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 使用 /api/auth 路径
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录认证相关接口")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录验证并返回JWT令牌")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserResponse userResponse = userService.login(request);

            // 生成JWT令牌
            String token = jwtUtil.generateToken(
                    userResponse.getUsername(),
                    userResponse.getRole().name(),
                    userResponse.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("user", userResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}