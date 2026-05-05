package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 调试控制器 - 用于排查登录问题
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Debug endpoint is working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                response.put("error", "User not found");
                response.put("username", username);
                return ResponseEntity.ok(response);
            }
            
            User user = userOpt.get();
            
            // 检查密码
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            
            response.put("username", username);
            response.put("userFound", true);
            response.put("passwordMatches", passwordMatches);
            response.put("userStatus", user.getStatus());
            response.put("userRole", user.getRole());
            response.put("storedPasswordHash", user.getPassword());
            
            // 生成新的哈希用于比较
            String newHash = passwordEncoder.encode(password);
            response.put("newPasswordHash", newHash);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}


