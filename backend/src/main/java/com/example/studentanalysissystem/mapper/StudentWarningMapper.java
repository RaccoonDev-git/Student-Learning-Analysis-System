package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.StudentWarningResponse;
import com.example.studentanalysissystem.model.StudentWarning;
import org.springframework.stereotype.Component;

/**
 * 学生预警Mapper
 */
@Component
public class StudentWarningMapper {

    public StudentWarningResponse toResponse(StudentWarning warning) {
        if (warning == null) {
            return null;
        }

        return StudentWarningResponse.builder()
                .id(warning.getId())
                .studentId(warning.getStudent().getId())
                .studentName(warning.getStudent().getName())
                .studentNumber(warning.getStudent().getStudentNumber())
                .courseId(warning.getCourse().getId())
                .courseCode(warning.getCourse().getCode())
                .courseName(warning.getCourse().getName())
                .warningType(warning.getWarningType())
                .warningLevel(warning.getWarningLevel())
                .title(warning.getTitle())
                .content(warning.getContent())
                .currentRegularScore(warning.getCurrentRegularScore())
                .warningThreshold(warning.getWarningThreshold())
                .isHandled(warning.getIsProcessed())
                .handledAt(warning.getHandledAt())
                .handledBy(warning.getHandledBy() != null ? Long.valueOf(warning.getHandledBy()) : null)
                .handleRemarks(warning.getHandleRemarks())
                .semester(warning.getSemester())
                .academicYear(warning.getAcademicYear())
                .createdAt(warning.getCreatedAt())
                .updatedAt(warning.getUpdatedAt())
                .build();
    }
}
