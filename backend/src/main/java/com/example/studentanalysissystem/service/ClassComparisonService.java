package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.ClassComparisonResponse;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 班级对比分析服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClassComparisonService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    /**
     * 获取多个班级的成绩对比分析
     * 
     * @param classNames 班级名称列表
     * @param courseId   可选的课程ID（如果指定，只比较该课程）
     * @param semester   可选的学期（如果指定，只比较该学期）
     * @return 班级对比分析结果
     */
    public ClassComparisonResponse compareClasses(List<String> classNames, Long courseId, String semester) {
        log.info("开始班级对比分析: classNames={}, courseId={}, semester={}", classNames, courseId, semester);

        if (classNames == null || classNames.isEmpty()) {
            log.warn("班级列表为空");
            return createEmptyResponse();
        }

        // 获取所有班级的统计数据
        List<ClassComparisonResponse.ClassStats> classStatsList = new ArrayList<>();

        for (String className : classNames) {
            ClassComparisonResponse.ClassStats stats = calculateClassStats(className, courseId, semester);
            if (stats != null) {
                classStatsList.add(stats);
            }
        }

        if (classStatsList.isEmpty()) {
            log.warn("没有找到任何班级数据");
            return createEmptyResponse();
        }

        // 排序班级（按平均分降序）
        classStatsList.sort((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()));

        // 计算总体统计
        double overallAverage = classStatsList.stream()
                .mapToDouble(ClassComparisonResponse.ClassStats::getAverageScore)
                .average()
                .orElse(0.0);

        String bestClass = classStatsList.get(0).getClassName();
        String worstClass = classStatsList.get(classStatsList.size() - 1).getClassName();

        return ClassComparisonResponse.builder()
                .classStatsList(classStatsList)
                .overallAverage(overallAverage)
                .bestClass(bestClass)
                .worstClass(worstClass)
                .totalClasses(classStatsList.size())
                .compareDate(new Date())
                .build();
    }

    /**
     * 计算单个班级的统计数据
     */
    private ClassComparisonResponse.ClassStats calculateClassStats(String className, Long courseId, String semester) {
        // 获取班级的所有学生
        List<Student> students = studentRepository.findByClassName(className);

        if (students.isEmpty()) {
            log.warn("班级 {} 没有学生数据", className);
            return null;
        }

        // 获取学生IDs
        List<Long> studentIds = students.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        // 获取成绩数据
        List<Grade> grades = new ArrayList<>();
        if (courseId != null) {
            // 指定课程
            grades = gradeRepository.findByCourseIdAndStudentIdIn(courseId, studentIds);
        } else {
            // 所有课程 - 为每个学生获取成绩
            for (Long studentId : studentIds) {
                grades.addAll(gradeRepository.findByStudentId(studentId));
            }
        }

        // 如果指定了学期，过滤学期（通过课程的学期字段）
        if (semester != null && !semester.isEmpty()) {
            grades = grades.stream()
                    .filter(g -> g.getCourse() != null && semester.equals(g.getCourse().getSemester()))
                    .collect(Collectors.toList());
        }

        if (grades.isEmpty()) {
            log.warn("班级 {} 没有成绩数据", className);
            return null;
        }

        // 计算统计数据
        List<Double> scores = grades.stream()
                .map(g -> g.getScore().doubleValue())
                .collect(Collectors.toList());

        double average = scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double maxScore = scores.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        double minScore = scores.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

        long passCount = scores.stream()
                .filter(score -> score >= 60)
                .count();

        double passRate = (double) passCount / scores.size() * 100;

        long excellentCount = scores.stream()
                .filter(score -> score >= 90)
                .count();

        double excellentRate = (double) excellentCount / scores.size() * 100;

        // 计算标准差
        double stdDeviation = calculateStdDeviation(scores, average);

        return ClassComparisonResponse.ClassStats.builder()
                .className(className)
                .studentCount(students.size())
                .gradeCount(grades.size())
                .averageScore(Math.round(average * 100.0) / 100.0)
                .maxScore(maxScore)
                .minScore(minScore)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .excellentRate(Math.round(excellentRate * 100.0) / 100.0)
                .stdDeviation(Math.round(stdDeviation * 100.0) / 100.0)
                .build();
    }

    /**
     * 计算标准差
     */
    private double calculateStdDeviation(List<Double> scores, double mean) {
        if (scores.size() <= 1) {
            return 0.0;
        }

        double variance = scores.stream()
                .mapToDouble(score -> Math.pow(score - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * 创建空响应
     */
    private ClassComparisonResponse createEmptyResponse() {
        return ClassComparisonResponse.builder()
                .classStatsList(new ArrayList<>())
                .overallAverage(0.0)
                .bestClass("无")
                .worstClass("无")
                .totalClasses(0)
                .compareDate(new Date())
                .build();
    }
}
