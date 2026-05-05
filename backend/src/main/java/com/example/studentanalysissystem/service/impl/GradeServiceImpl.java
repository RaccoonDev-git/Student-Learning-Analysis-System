package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.SubmitGradeRequest;
import com.example.studentanalysissystem.dto.response.GradeResponse;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.mapper.GradeMapper;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import com.example.studentanalysissystem.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeMapper gradeMapper;

    @Override
    @Transactional
    public GradeResponse submitGrade(SubmitGradeRequest request) {
        // 检查学生是否存在
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        // 检查课程是否存在
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // 创建成绩实体
        Grade grade = Grade.builder()
                .student(student)
                .course(course)
                .examType(request.getExamType())
                .score(request.getScore())
                .totalScore(request.getTotalScore())
                .examDate(request.getExamDate())
                .remarks(request.getRemarks())
                .build();

        // 保存时会自动计算百分比和等级(@PrePersist)
        Grade savedGrade = gradeRepository.save(grade);
        return gradeMapper.toResponse(savedGrade);
    }

    @Override
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        return gradeMapper.toResponse(grade);
    }

    @Override
    public List<GradeResponse> getGradesByStudentId(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return gradeMapper.toResponseList(grades);
    }

    @Override
    public List<GradeResponse> getGradesByCourseId(Long courseId) {
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        return gradeMapper.toResponseList(grades);
    }

    @Override
    public List<GradeResponse> getGradesByStudentAndCourse(Long studentId, Long courseId) {
        List<Grade> grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        return gradeMapper.toResponseList(grades);
    }

    @Override
    public List<GradeResponse> getStudentGradesBySemester(Long studentId, String semester) {
        List<Grade> grades = gradeRepository.findByStudentAndSemester(studentId, semester);
        return gradeMapper.toResponseList(grades);
    }

    @Override
    @Transactional
    public GradeResponse updateGrade(Long id, SubmitGradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));

        // 更新成绩信息
        grade.setExamType(request.getExamType());
        grade.setScore(request.getScore());
        grade.setTotalScore(request.getTotalScore());
        grade.setExamDate(request.getExamDate());
        grade.setRemarks(request.getRemarks());

        // 保存时会自动重新计算百分比和等级(@PreUpdate)
        Grade updatedGrade = gradeRepository.save(grade);
        return gradeMapper.toResponse(updatedGrade);
    }

    @Override
    @Transactional
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        gradeRepository.delete(grade);
    }

    @Override
    public BigDecimal calculateStudentAverageScore(Long studentId) {
        BigDecimal average = gradeRepository.calculateAverageScoreByStudent(studentId);
        return average != null ? average : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateCourseAverageScore(Long courseId) {
        BigDecimal average = gradeRepository.calculateAverageScoreByCourse(courseId);
        return average != null ? average : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Long> getCourseGradeDistribution(Long courseId) {
        List<Object[]> distribution = gradeRepository.findGradeDistributionByCourse(courseId);
        Map<String, Long> result = new HashMap<>();

        for (Object[] row : distribution) {
            String gradeLevel = (String) row[0];
            Long count = (Long) row[1];
            result.put(gradeLevel, count);
        }

        return result;
    }

    @Override
    public List<GradeResponse> getAllGrades() {
        List<Grade> grades = gradeRepository.findAll();
        return gradeMapper.toResponseList(grades);
    }

    @Override
    public List<GradeResponse> getFailingGrades() {
        List<Grade> grades = gradeRepository.findFailingGrades();
        return gradeMapper.toResponseList(grades);
    }
}
