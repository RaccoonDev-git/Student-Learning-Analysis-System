package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.SubmitGradeRequest;
import com.example.studentanalysissystem.dto.response.GradeResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 */
public interface GradeService {

    /**
     * 提交成绩
     */
    GradeResponse submitGrade(SubmitGradeRequest request);

    /**
     * 根据ID查询成绩
     */
    GradeResponse getGradeById(Long id);

    /**
     * 查询学生的所有成绩
     */
    List<GradeResponse> getGradesByStudentId(Long studentId);

    /**
     * 查询课程的所有成绩
     */
    List<GradeResponse> getGradesByCourseId(Long courseId);

    /**
     * 查询学生在某课程的成绩
     */
    List<GradeResponse> getGradesByStudentAndCourse(Long studentId, Long courseId);

    /**
     * 查询学生某学期的成绩
     */
    List<GradeResponse> getStudentGradesBySemester(Long studentId, String semester);

    /**
     * 更新成绩
     */
    GradeResponse updateGrade(Long id, SubmitGradeRequest request);

    /**
     * 删除成绩
     */
    void deleteGrade(Long id);

    /**
     * 计算学生平均分
     */
    BigDecimal calculateStudentAverageScore(Long studentId);

    /**
     * 计算课程平均分
     */
    BigDecimal calculateCourseAverageScore(Long courseId);

    /**
     * 查询课程成绩分布
     */
    Map<String, Long> getCourseGradeDistribution(Long courseId);

    /**
     * 获取所有成绩
     */
    List<GradeResponse> getAllGrades();

    /**
     * 查询不及格成绩
     */
    List<GradeResponse> getFailingGrades();
}
