package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.response.ResourceResponse;
import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.service.ResourceService;
import com.example.studentanalysissystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学资源控制器
 */
@Tag(name = "教学资源管理", description = "教学资源的增删改查接口")
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final UserService userService;

    @Operation(summary = "上传资源")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ResourceResponse> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("uploaderId") Long uploaderId) {

        UserResponse user = userService.getUserById(uploaderId);

        ResourceResponse response = resourceService.uploadResource(
                file,
                name,
                description,
                uploaderId,
                user.getUsername(),
                courseId,
                category);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "下载资源")
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Resource> downloadResource(@PathVariable Long id) {
        Resource resource = resourceService.downloadResource(id);
        ResourceResponse resourceInfo = resourceService.getResourceById(id);

        // 增加下载次数
        resourceService.incrementDownloadCount(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resourceInfo.getOriginalFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "获取资源详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ResourceResponse> getResource(@PathVariable Long id) {
        ResourceResponse response = resourceService.getResourceById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取所有资源")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        List<ResourceResponse> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "按类型筛选资源")
    @GetMapping("/type/{fileType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<ResourceResponse>> getResourcesByType(@PathVariable String fileType) {
        List<ResourceResponse> resources = resourceService.getResourcesByType(fileType);
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "获取我上传的资源")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ResourceResponse>> getMyResources(
            @RequestParam("uploaderId") Long uploaderId) {
        List<ResourceResponse> resources = resourceService.getResourcesByUploader(uploaderId);
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "搜索资源")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<ResourceResponse>> searchResources(
            @RequestParam String keyword) {
        List<ResourceResponse> resources = resourceService.searchResources(keyword);
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "综合筛选资源")
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<ResourceResponse>> filterResources(
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long uploaderId) {
        List<ResourceResponse> resources = resourceService.filterResources(fileType, category, uploaderId);
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Map<String, String>> deleteResource(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        resourceService.deleteResource(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "资源删除成功");
        return ResponseEntity.ok(response);
    }
}
