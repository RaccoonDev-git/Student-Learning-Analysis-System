package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.EnrollCourseRequest;
import com.example.studentanalysissystem.dto.response.EnrollmentResponse;
import com.example.studentanalysissystem.model.CourseEnrollment;

import java.util.List;

/**
 * 选课服务接口
 */
public interface EnrollmentService {

    /**
     * 学生选课
     */
    EnrollmentResponse enrollCourse(EnrollCourseRequest request);

    /**
     * 学生退课
     */
    EnrollmentResponse dropCourse(Long enrollmentId);

    /**
     * 根据ID查询选课记录
     */
    EnrollmentResponse getEnrollmentById(Long id);

    /**
     * 查询学生的所有选课记录
     */
    List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId);

    /**
     * 查询课程的所有选课记录
     */
    List<EnrollmentResponse> getEnrollmentsByCourseId(Long courseId);

    /**
     * 查询学生在某学期的选课记录
     */
    List<EnrollmentResponse> getStudentEnrollmentsBySemester(Long studentId, String semester);

    /**
     * 检查学生是否已选某课程
     */
    boolean isStudentEnrolled(Long studentId, Long courseId);

    /**
     * 更新选课状态
     */
    EnrollmentResponse updateEnrollmentStatus(Long id, CourseEnrollment.EnrollmentStatus status);
}
