package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.service.DynamicTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态模板控制器
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "动态模板", description = "根据课程配置生成动态导入模板")
@CrossOrigin(origins = "*")
public class DynamicTemplateController {

    private final DynamicTemplateService dynamicTemplateService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "生成课程专用模板", description = "根据课程ID生成对应的导入模板")
    public ResponseEntity<byte[]> generateCourseTemplate(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            byte[] template = dynamicTemplateService.generateCourseTemplate(courseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "course_" + courseId + "_template.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/preset/{courseType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "生成预设模板", description = "根据课程类型生成预设的导入模板")
    public ResponseEntity<byte[]> generatePresetTemplate(
            @Parameter(description = "课程类型") @PathVariable String courseType) {
        try {
            byte[] template = dynamicTemplateService.generatePresetTemplate(courseType);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", courseType + "_template.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/generic")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "生成通用模板", description = "生成基于所有平时分类型的通用模板")
    public ResponseEntity<byte[]> generateGenericTemplate() {
        try {
            byte[] template = dynamicTemplateService.generateGenericTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "generic_template.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/course-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取支持的课程类型", description = "获取所有支持的课程类型列表")
    public ResponseEntity<List<String>> getSupportedCourseTypes() {
        List<String> courseTypes = dynamicTemplateService.getSupportedCourseTypes();
        return ResponseEntity.ok(courseTypes);
    }
}
