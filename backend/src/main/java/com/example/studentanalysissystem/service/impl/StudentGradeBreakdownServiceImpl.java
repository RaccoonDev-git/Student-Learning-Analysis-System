package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.response.StudentGradeBreakdownResponse;
import com.example.studentanalysissystem.dto.response.WeightConfigResponse;
import com.example.studentanalysissystem.model.CourseWeightConfig;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.repository.CourseWeightConfigRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.service.StudentGradeBreakdownService;
import com.example.studentanalysissystem.service.WeightConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentGradeBreakdownServiceImpl implements StudentGradeBreakdownService {

    private final GradeRepository gradeRepository;
    private final CourseWeightConfigRepository courseWeightConfigRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final WeightConfigService weightConfigService;

    @Override
    public StudentGradeBreakdownResponse getStudentGradeBreakdown(Long studentId, Long courseId) {
        log.info("获取学生{}在课程{}的成绩明细", studentId, courseId);
        
        // 获取学生信息
        String studentName = userRepository.findById(studentId)
            .map(user -> user.getUsername())
            .orElse("未知学生");
        
        // 获取课程信息
        String courseName = courseRepository.findById(courseId)
            .map(course -> course.getName())
            .orElse("未知课程");
        
        // 获取权重配置
        WeightConfigResponse weightConfig = weightConfigService.getWeightConfigByCourseId(courseId);
        
        // 获取学生的所有成绩
        List<Grade> allGrades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        // 按考试类型分组
        Map<String, List<Grade>> gradesByType = allGrades.stream()
            .collect(Collectors.groupingBy(Grade::getExamType));
        
        // 构建各项成绩明细
        List<StudentGradeBreakdownResponse.GradeDetail> attendanceGrades = 
            buildGradeDetails(gradesByType.get("attendance"));
        List<StudentGradeBreakdownResponse.GradeDetail> homeworkGrades = 
            buildGradeDetails(gradesByType.get("homework"));
        List<StudentGradeBreakdownResponse.GradeDetail> labGrades = 
            buildGradeDetails(gradesByType.get("lab"));
        List<StudentGradeBreakdownResponse.GradeDetail> quizGrades = 
            buildGradeDetails(gradesByType.get("quiz"));
        List<StudentGradeBreakdownResponse.GradeDetail> midtermGrades = 
            buildGradeDetails(gradesByType.get("midterm"));
        
        // 计算各项平均分
        BigDecimal attendanceAverage = calculateAverage(attendanceGrades);
        BigDecimal homeworkAverage = calculateAverage(homeworkGrades);
        BigDecimal labAverage = calculateAverage(labGrades);
        BigDecimal quizAverage = calculateAverage(quizGrades);
        BigDecimal midtermAverage = calculateAverage(midtermGrades);
        
        // 计算加权分
        BigDecimal attendanceWeighted = attendanceAverage.multiply(weightConfig.getAttendanceWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal homeworkWeighted = homeworkAverage.multiply(weightConfig.getHomeworkWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal labWeighted = labAverage.multiply(weightConfig.getLabWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal quizWeighted = quizAverage.multiply(weightConfig.getQuizWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal midtermWeighted = midtermAverage.multiply(weightConfig.getMidtermWeight()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // 计算总成绩
        BigDecimal totalScore = attendanceWeighted.add(homeworkWeighted).add(labWeighted)
            .add(quizWeighted).add(midtermWeighted);
        
        return StudentGradeBreakdownResponse.builder()
            .studentId(studentId)
            .studentName(studentName)
            .courseId(courseId)
            .courseName(courseName)
            .attendanceWeight(weightConfig.getAttendanceWeight())
            .homeworkWeight(weightConfig.getHomeworkWeight())
            .labWeight(weightConfig.getLabWeight())
            .quizWeight(weightConfig.getQuizWeight())
            .midtermWeight(weightConfig.getMidtermWeight())
            .isDefaultWeight(weightConfig.isDefault())
            .attendanceGrades(attendanceGrades)
            .homeworkGrades(homeworkGrades)
            .labGrades(labGrades)
            .quizGrades(quizGrades)
            .midtermGrades(midtermGrades)
            .attendanceAverage(attendanceAverage)
            .attendanceWeighted(attendanceWeighted)
            .homeworkAverage(homeworkAverage)
            .homeworkWeighted(homeworkWeighted)
            .labAverage(labAverage)
            .labWeighted(labWeighted)
            .quizAverage(quizAverage)
            .quizWeighted(quizWeighted)
            .midtermAverage(midtermAverage)
            .midtermWeighted(midtermWeighted)
            .totalScore(totalScore)
            .build();
    }

    @Override
    public List<StudentGradeBreakdownResponse> getAllStudentGradeBreakdowns(Long studentId) {
        log.info("获取学生{}所有课程的成绩明细", studentId);
        
        // 获取学生所有有成绩的课程
        List<Grade> studentGrades = gradeRepository.findByStudentId(studentId);
        List<Long> courseIds = studentGrades.stream()
            .map(grade -> grade.getCourse().getId())
            .distinct()
            .collect(Collectors.toList());
        
        return courseIds.stream()
            .map(courseId -> getStudentGradeBreakdown(studentId, courseId))
            .collect(Collectors.toList());
    }
    
    private List<StudentGradeBreakdownResponse.GradeDetail> buildGradeDetails(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return List.of();
        }
        
        return grades.stream()
            .map(grade -> StudentGradeBreakdownResponse.GradeDetail.builder()
                .gradeId(grade.getId())
                .examName(grade.getExamType())
                .score(grade.getScore())
                .examDate(grade.getExamDate() != null ? grade.getExamDate().toString() : "")
                .description(grade.getRemarks())
                .build())
            .collect(Collectors.toList());
    }
    
    private BigDecimal calculateAverage(List<StudentGradeBreakdownResponse.GradeDetail> grades) {
        if (grades == null || grades.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = grades.stream()
            .map(StudentGradeBreakdownResponse.GradeDetail::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return sum.divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);
    }
}
