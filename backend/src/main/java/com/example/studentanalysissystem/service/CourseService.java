package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.CreateCourseRequest;
import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.model.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 创建课程
     */
    CourseResponse createCourse(CreateCourseRequest request);

    /**
     * 根据ID查询课程
     */
    CourseResponse getCourseById(Long id);

    /**
     * 根据课程代码查询课程
     */
    CourseResponse getCourseByCode(String code);

    /**
     * 查询所有课程
     */
    List<CourseResponse> getAllCourses();

    /**
     * 根据教师ID查询课程
     */
    List<CourseResponse> getCoursesByTeacherId(Long teacherId);

    /**
     * 根据学期查询课程
     */
    List<CourseResponse> getCoursesBySemester(String semester);

    /**
     * 根据状态查询课程
     */
    List<CourseResponse> getCoursesByStatus(Course.CourseStatus status);

    /**
     * 更新课程信息
     */
    CourseResponse updateCourse(Long id, com.example.studentanalysissystem.dto.request.UpdateCourseRequest request);

    /**
     * 删除课程
     */
    void deleteCourse(Long id);

    /**
     * 更新课程状态
     */
    CourseResponse updateCourseStatus(Long id, Course.CourseStatus status);

    /**
     * 搜索课程 (根据课程代码或名称)
     */
    List<CourseResponse> searchCourses(String keyword);
}
