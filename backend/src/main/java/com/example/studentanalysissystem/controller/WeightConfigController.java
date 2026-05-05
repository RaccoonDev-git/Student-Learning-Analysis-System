package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.dto.request.WeightConfigRequest;
import com.example.studentanalysissystem.dto.response.WeightConfigResponse;
import com.example.studentanalysissystem.service.WeightConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weight-config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "课程权重配置管理", description = "课程权重配置的增删改查")
@CrossOrigin(origins = "*")
public class WeightConfigController {

    private final WeightConfigService weightConfigService;

    @GetMapping("/course/{courseId}")
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    @Operation(summary = "获取课程权重配置", description = "根据课程ID获取权重配置")
    public ResponseEntity<WeightConfigResponse> getWeightConfigByCourseId(@PathVariable Long courseId) {
        log.info("获取课程{}的权重配置", courseId);
        WeightConfigResponse config = weightConfigService.getWeightConfigByCourseId(courseId);
        return ResponseEntity.ok(config);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "创建或更新权重配置", description = "创建或更新课程的权重配置")
    public ResponseEntity<WeightConfigResponse> createOrUpdateWeightConfig(@Valid @RequestBody WeightConfigRequest request) {
        log.info("创建或更新权重配置: {}", request);
        WeightConfigResponse response = weightConfigService.createOrUpdateWeightConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "获取所有权重配置", description = "获取所有课程的权重配置")
    public ResponseEntity<List<WeightConfigResponse>> getAllWeightConfigs() {
        log.info("获取所有权重配置");
        List<WeightConfigResponse> configs = weightConfigService.getAllWeightConfigs();
        return ResponseEntity.ok(configs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "删除权重配置", description = "删除指定的权重配置")
    public ResponseEntity<Void> deleteWeightConfig(@PathVariable Long id) {
        log.info("删除权重配置: {}", id);
        weightConfigService.deleteWeightConfig(id);
        return ResponseEntity.noContent().build();
    }
}
