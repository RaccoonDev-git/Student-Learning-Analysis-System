package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.service.GradeExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 数据导出控制器
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Export", description = "数据导出接口")
@CrossOrigin(origins = "*")
public class ExportController {

    private final GradeExportService gradeExportService;

    /**
     * 导出所有成绩
     */
    @GetMapping("/grades/all")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "导出所有成绩", description = "导出系统中所有学生的成绩数据为Excel文件")
    public ResponseEntity<byte[]> exportAllGrades() {
        try {
            byte[] excelData = gradeExportService.exportAllGrades();
            String filename = gradeExportService.generateFileName("所有");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            log.error("导出所有成绩失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导出指定学生的成绩
     */
    @GetMapping("/grades/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "导出学生成绩", description = "导出指定学生的所有成绩数据")
    public ResponseEntity<byte[]> exportStudentGrades(@PathVariable Long studentId) {
        try {
            byte[] excelData = gradeExportService.exportStudentGrades(studentId);
            String filename = gradeExportService.generateFileName("学生" + studentId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            log.error("导出学生{}成绩失败", studentId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导出指定课程的成绩
     */
    @GetMapping("/grades/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "导出课程成绩", description = "导出指定课程的所有学生成绩")
    public ResponseEntity<byte[]> exportCourseGrades(@PathVariable Long courseId) {
        try {
            byte[] excelData = gradeExportService.exportCourseGrades(courseId);
            String filename = gradeExportService.generateFileName("课程" + courseId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            log.error("导出课程{}成绩失败", courseId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
