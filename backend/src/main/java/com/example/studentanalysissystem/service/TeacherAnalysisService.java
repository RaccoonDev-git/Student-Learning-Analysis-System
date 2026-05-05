package com.example.studentanalysissystem.service;

import java.util.Map;

/**
 * 教师分析服务接口
 */
public interface TeacherAnalysisService {

    /**
     * 分析学生学习轨迹
     * @param studentId 学生ID
     * @param courseId 课程ID（可选，如果提供则只分析该课程）
     * @param semester 学期
     * @param academicYear 学年
     * @return 分析结果
     */
    Map<String, Object> analyzeStudentTrajectory(Long studentId, Long courseId, String semester, String academicYear);

    /**
     * 分析课程教学效果
     * @param courseId 课程ID
     * @param semester 学期
     * @param academicYear 学年
     * @return 分析结果
     */
    Map<String, Object> analyzeCourseEffectiveness(Long courseId, String semester, String academicYear);

    /**
     * 分析班级学习氛围
     * @param classId 班级ID
     * @param semester 学期
     * @param academicYear 学年
     * @return 分析结果
     */
    Map<String, Object> analyzeClassAtmosphere(String classId, String semester, String academicYear);

    /**
     * 对比班级学习氛围
     * @param teacherId 教师ID
     * @param semester 学期
     * @param academicYear 学年
     * @return 对比结果
     */
    Map<String, Object> compareClasses(Long teacherId, String semester, String academicYear);

}
