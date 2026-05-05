package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 成绩导出服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeExportService {

    private final GradeRepository gradeRepository;
    private final ExcelExportService excelService;

    /**
     * 导出所有成绩
     */
    public byte[] exportAllGrades() throws IOException {
        List<Grade> grades = gradeRepository.findAll();
        return exportGrades(grades, "所有成绩");
    }

    /**
     * 导出指定学生的成绩
     */
    public byte[] exportStudentGrades(Long studentId) throws IOException {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return exportGrades(grades, "学生成绩_ID" + studentId);
    }

    /**
     * 导出指定课程的成绩
     */
    public byte[] exportCourseGrades(Long courseId) throws IOException {
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        return exportGrades(grades, "课程成绩_ID" + courseId);
    }

    /**
     * 导出成绩数据到Excel
     */
    private byte[] exportGrades(List<Grade> grades, String sheetName) throws IOException {
        Workbook workbook = excelService.createWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建样式
        CellStyle headerStyle = excelService.createHeaderStyle(workbook);
        CellStyle dataStyle = excelService.createDataStyle(workbook);

        // 创建标题行
        List<String> headers = Arrays.asList(
                "学生ID", "学生姓名", "课程ID", "课程名称",
                "考试类型", "成绩", "总分", "百分比", "等级", "考试日期", "备注");
        excelService.createHeaderRow(sheet, headers, headerStyle);

        // 填充数据
        int rowNum = 1;
        for (Grade grade : grades) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            // 学生信息
            Long studentId = grade.getStudent() != null ? grade.getStudent().getId() : null;
            String studentName = grade.getStudent() != null ? grade.getStudent().getName() : "";
            excelService.setCellValue(row.createCell(colNum++), studentId, dataStyle);
            excelService.setCellValue(row.createCell(colNum++), studentName, dataStyle);

            // 课程信息
            Long courseId = grade.getCourse() != null ? grade.getCourse().getId() : null;
            String courseName = grade.getCourse() != null ? grade.getCourse().getName() : "";
            excelService.setCellValue(row.createCell(colNum++), courseId, dataStyle);
            excelService.setCellValue(row.createCell(colNum++), courseName, dataStyle);

            // 成绩信息
            excelService.setCellValue(row.createCell(colNum++), grade.getExamType(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getScore(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getTotalScore(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getPercentage(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getGradeLevel(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getExamDate(), dataStyle);
            excelService.setCellValue(row.createCell(colNum++), grade.getRemarks(), dataStyle);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024); // 额外增加宽度
        }

        return excelService.workbookToBytes(workbook);
    }

    /**
     * 生成文件名
     */
    public String generateFileName(String type) {
        return excelService.generateFileName("成绩报表_" + type);
    }
}
