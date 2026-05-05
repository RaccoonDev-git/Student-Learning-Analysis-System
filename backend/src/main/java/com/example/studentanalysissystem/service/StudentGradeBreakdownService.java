package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.StudentGradeBreakdownResponse;
import java.util.List;

public interface StudentGradeBreakdownService {
    
    /**
     * 获取学生的成绩明细
     */
    StudentGradeBreakdownResponse getStudentGradeBreakdown(Long studentId, Long courseId);
    
    /**
     * 获取学生所有课程的成绩明细
     */
    List<StudentGradeBreakdownResponse> getAllStudentGradeBreakdowns(Long studentId);
}
