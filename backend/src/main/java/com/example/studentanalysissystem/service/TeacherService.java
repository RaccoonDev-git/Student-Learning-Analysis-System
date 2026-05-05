package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.CreateTeacherRequest;
import com.example.studentanalysissystem.dto.request.UpdateTeacherRequest;
import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.dto.response.TeacherResponse;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService {

    /**
     * 创建教师
     */
    TeacherResponse createTeacher(CreateTeacherRequest request);

    /**
     * 根据ID查询教师
     */
    TeacherResponse getTeacherById(Long id);

    /**
     * 根据工号查询教师
     */
    TeacherResponse getTeacherByEmployeeNumber(String employeeNumber);

    /**
     * 根据用户ID查询教师
     */
    TeacherResponse getTeacherByUserId(Long userId);

    /**
     * 查询所有教师
     */
    List<TeacherResponse> getAllTeachers();

    /**
     * 根据部门查询教师
     */
    List<TeacherResponse> getTeachersByDepartment(String department);

    /**
     * 根据职称查询教师
     */
    List<TeacherResponse> getTeachersByTitle(String title);

    /**
     * 更新教师信息
     */
    TeacherResponse updateTeacher(Long id, UpdateTeacherRequest request);

    /**
     * 删除教师
     */
    void deleteTeacher(Long id);

    /**
     * 搜索教师 (根据姓名或工号)
     */
    List<TeacherResponse> searchTeachers(String keyword);

    /**
     * 获取教师所教班级
     */
    List<String> getTeacherClasses(Long teacherId);

    /**
     * 获取教师课程
     */
    List<CourseResponse> getTeacherCourses(Long teacherId);

    /**
     * 获取教师在指定班级的课程
     */
    List<CourseResponse> getTeacherCoursesInClass(Long teacherId, String className);


    /**
     * 获取教师管理的学生
     */
    List<com.example.studentanalysissystem.dto.response.StudentResponse> getTeacherStudents(Long teacherId);
}