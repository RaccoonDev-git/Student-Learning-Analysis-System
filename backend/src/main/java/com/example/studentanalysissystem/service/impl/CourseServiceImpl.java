package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.CreateCourseRequest;
import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.exception.DuplicateResourceException;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.mapper.CourseMapper;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.Teacher;
import com.example.studentanalysissystem.repository.CourseEnrollmentRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.TeacherRepository;
import com.example.studentanalysissystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        // 检查教师是否存在
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));

        // 检查课程代码是否已存在
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course", "code", request.getCode());
        }

        // 创建课程实体
        Course course = Course.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .teacher(teacher)
                .credits(request.getCredits())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .maxStudents(request.getMaxStudents())
                .status(request.getStatus() != null ? request.getStatus() : Course.CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);
        CourseResponse response = courseMapper.toResponse(savedCourse);

        // 设置选课人数
        Long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(
                savedCourse.getId(),
                com.example.studentanalysissystem.model.CourseEnrollment.EnrollmentStatus.ENROLLED);
        response.setEnrolledCount(enrolledCount.intValue());

        return response;
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        CourseResponse response = courseMapper.toResponse(course);

        // 设置选课人数
        Long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(
                id,
                com.example.studentanalysissystem.model.CourseEnrollment.EnrollmentStatus.ENROLLED);
        response.setEnrolledCount(enrolledCount.intValue());

        return response;
    }

    @Override
    public CourseResponse getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "code", code));
        return courseMapper.toResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toResponseList(courses);
    }

    @Override
    public List<CourseResponse> getCoursesByTeacherId(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        return courseMapper.toResponseList(courses);
    }

    @Override
    public List<CourseResponse> getCoursesBySemester(String semester) {
        List<Course> courses = courseRepository.findBySemester(semester);
        return courseMapper.toResponseList(courses);
    }

    @Override
    public List<CourseResponse> getCoursesByStatus(Course.CourseStatus status) {
        List<Course> courses = courseRepository.findByStatus(status);
        return courseMapper.toResponseList(courses);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id,
            com.example.studentanalysissystem.dto.request.UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        // 更新课程信息(只更新非空字段)
        if (request.getCourseName() != null) {
            course.setName(request.getCourseName());
        }
        if (request.getCredits() != null) {
            course.setCredits(request.getCredits());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getMaxStudents() != null) {
            course.setMaxStudents(request.getMaxStudents());
        }

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourseStatus(Long id, Course.CourseStatus status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setStatus(status);
        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    public List<CourseResponse> searchCourses(String keyword) {
        List<Course> courses = courseRepository.searchByKeyword(keyword);
        return courseMapper.toResponseList(courses);
    }
}
