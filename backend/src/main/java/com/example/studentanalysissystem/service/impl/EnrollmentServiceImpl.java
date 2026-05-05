package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.EnrollCourseRequest;
import com.example.studentanalysissystem.dto.response.EnrollmentResponse;
import com.example.studentanalysissystem.exception.BusinessException;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.mapper.EnrollmentMapper;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.CourseEnrollment;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.repository.CourseEnrollmentRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import com.example.studentanalysissystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(EnrollCourseRequest request) {
        // 检查学生是否存在
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        // 检查课程是否存在
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // 检查课程状态
        if (course.getStatus() != Course.CourseStatus.ACTIVE) {
            throw new BusinessException("该课程不在开放选课状态");
        }

        // 检查是否已选该课程
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new BusinessException("学生已选择该课程");
        }

        // 检查课程人数限制
        if (course.getMaxStudents() != null) {
            Long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(
                    request.getCourseId(),
                    CourseEnrollment.EnrollmentStatus.ENROLLED);
            if (enrolledCount >= course.getMaxStudents()) {
                throw new BusinessException("该课程选课人数已满");
            }
        }

        // 创建选课记录
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDateTime.now())
                .status(CourseEnrollment.EnrollmentStatus.ENROLLED)
                .build();

        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    @Transactional
    public EnrollmentResponse dropCourse(Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", "id", enrollmentId));

        if (enrollment.getStatus() == CourseEnrollment.EnrollmentStatus.DROPPED) {
            throw new BusinessException("该选课记录已被取消");
        }

        enrollment.setStatus(CourseEnrollment.EnrollmentStatus.DROPPED);
        CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(updatedEnrollment);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        CourseEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", "id", id));
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourseId(Long courseId) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Override
    public List<EnrollmentResponse> getStudentEnrollmentsBySemester(Long studentId, String semester) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByStudentAndSemester(studentId, semester);
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Override
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long id, CourseEnrollment.EnrollmentStatus status) {
        CourseEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", "id", id));

        enrollment.setStatus(status);
        CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(updatedEnrollment);
    }
}
