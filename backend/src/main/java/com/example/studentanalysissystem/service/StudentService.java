package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.CreateStudentRequest;
import com.example.studentanalysissystem.dto.response.StudentResponse;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {

    /**
     * 创建学生
     */
    StudentResponse createStudent(CreateStudentRequest request);

    /**
     * 根据ID查询学生
     */
    StudentResponse getStudentById(Long id);

    /**
     * 根据学号查询学生
     */
    StudentResponse getStudentByStudentNumber(String studentNumber);

    /**
     * 根据用户ID查询学生
     */
    StudentResponse getStudentByUserId(Long userId);

    /**
     * 查询所有学生
     */
    List<StudentResponse> getAllStudents();

    /**
     * 根据班级查询学生
     */
    List<StudentResponse> getStudentsByClassName(String className);

    /**
     * 根据年级查询学生
     */
    List<StudentResponse> getStudentsByGradeLevel(Integer gradeLevel);

    /**
     * 根据专业查询学生
     */
    List<StudentResponse> getStudentsByMajor(String major);

    /**
     * 更新学生信息
     */
    StudentResponse updateStudent(Long id, com.example.studentanalysissystem.dto.request.UpdateStudentRequest request);

    /**
     * 删除学生
     */
    void deleteStudent(Long id);

    /**
     * 批量删除学生
     */
    void batchDeleteStudents(List<Long> ids);

    /**
     * 搜索学生 (根据姓名或学号)
     */
    List<StudentResponse> searchStudents(String keyword);

    /**
     * 高级筛选学生
     */
    List<StudentResponse> filterStudents(Integer gradeLevel, String className, String major, String keyword);

    /**
     * 根据ID获取学生实体对象（用于内部操作）
     */
    com.example.studentanalysissystem.model.Student getStudentEntityById(Long id);

    /**
     * 根据课程ID获取选修该课程的班级列表
     */
    List<String> getClassesByCourseId(Long courseId);

    /**
     * 获取指定课程和班级的学生成绩数据
     */
    List<Map<String, Object>> getStudentScoresByCourseAndClass(Long courseId, String className);
}
