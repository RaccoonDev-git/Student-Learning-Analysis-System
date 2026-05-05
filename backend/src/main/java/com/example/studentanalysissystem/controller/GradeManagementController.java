package com.example.studentanalysissystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 成绩管理控制器
 * 提供成绩重新计算、数据一致性检查等管理功能
 */
@RestController
@RequestMapping("/api/grade-management")
@RequiredArgsConstructor
@Tag(name = "成绩管理", description = "成绩重新计算、数据一致性检查等管理功能")
@CrossOrigin(origins = "*")
public class GradeManagementController {

    @PostMapping("/recalculate/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "重新计算课程成绩", description = "注意：综合成绩功能已移除")
    public ResponseEntity<String> recalculateCourseGrades(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return ResponseEntity.badRequest()
                .body("综合成绩功能已移除，此接口不再可用");
    }

    @PostMapping("/recalculate/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "重新计算所有成绩", description = "注意：综合成绩功能已移除")
    public ResponseEntity<String> recalculateAllGrades() {
        return ResponseEntity.badRequest()
                .body("综合成绩功能已移除，此接口不再可用");
    }

    @PostMapping("/check-consistency")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "检查数据一致性", description = "注意：综合成绩功能已移除")
    public ResponseEntity<String> checkDataConsistency(
            @Parameter(description = "课程ID，不提供则检查所有课程") @RequestParam(required = false) Long courseId) {
        return ResponseEntity.badRequest()
                .body("综合成绩功能已移除，此接口不再可用");
    }
}
