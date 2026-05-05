package com.example.studentanalysissystem.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 静态资源控制器
 * 处理默认头像等静态资源
 */
@RestController
@RequestMapping("/api/static")
public class StaticResourceController {

    /**
     * 获取默认头像（JPG格式）
     */
    @GetMapping("/default-avatar.jpg")
    public ResponseEntity<Resource> getDefaultAvatarJpg() {
        try {
            Resource resource = new ClassPathResource("static/default avatar.jpg");
            
            if (!resource.exists()) {
                // 如果默认头像文件不存在，返回404
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // 缓存1年
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取默认头像的SVG版本（使用用户提供的自定义头像）
     */
    @GetMapping("/default-avatar.svg")
    public ResponseEntity<Resource> getDefaultAvatarSvg() {
        try {
            Resource resource = new ClassPathResource("static/default-avatar.svg");
            
            if (!resource.exists()) {
                // 如果自定义默认头像不存在，返回内置的默认头像
                String fallbackSvg = """
                    <svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
                        <defs>
                            <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                                <stop offset="0%" style="stop-color:#667eea;stop-opacity:1" />
                                <stop offset="100%" style="stop-color:#764ba2;stop-opacity:1" />
                            </linearGradient>
                        </defs>
                        <circle cx="100" cy="100" r="100" fill="url(#grad1)"/>
                        <circle cx="100" cy="80" r="30" fill="white" opacity="0.8"/>
                        <ellipse cx="100" cy="150" rx="50" ry="25" fill="white" opacity="0.8"/>
                    </svg>
                    """;
                    
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("image/svg+xml"))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                        .body(new org.springframework.core.io.ByteArrayResource(fallbackSvg.getBytes()));
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/svg+xml"))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
