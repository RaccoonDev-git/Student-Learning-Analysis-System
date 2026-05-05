package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.GradeStatisticsResponse;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩分析服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeAnalysisService {

    private final GradeRepository gradeRepository;
    // 注意：当前统计逻辑未直接使用到学生Repository，但保留用于后续扩展
    @SuppressWarnings("unused")
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * 获取成绩统计分析
     *
     * @param courseId   课程ID(可选)
     * @param className  班级名称(可选)
     * @param major      专业(可选)
     * @param semester   学期(可选)
     * @param gradeLevel 年级(可选)
     * @return 成绩统计结果
     */
    public GradeStatisticsResponse getGradeStatistics(
            Long courseId,
            String className,
            String major,
            String semester,
            Integer gradeLevel) {

        log.info("获取成绩统计: courseId={}, className={}, major={}, semester={}, gradeLevel={}",
                courseId, className, major, semester, gradeLevel);

        // 获取所有成绩
        List<Grade> allGrades = gradeRepository.findAll();

        // 根据条件筛选成绩
        List<Grade> filteredGrades = filterGrades(allGrades, courseId, className, major, semester, gradeLevel);

        if (filteredGrades.isEmpty()) {
            log.warn("没有找到符合条件的成绩数据");
            return createEmptyStatistics();
        }

        // 计算统计数据
        return calculateStatistics(filteredGrades, courseId, className, major, semester, gradeLevel);
    }

    /**
     * 根据课程ID获取成绩统计
     */
    public GradeStatisticsResponse getStatisticsByCourse(Long courseId) {
        return getGradeStatistics(courseId, null, null, null, null);
    }

    /**
     * 根据班级获取成绩统计
     */
    public GradeStatisticsResponse getStatisticsByClass(String className) {
        return getGradeStatistics(null, className, null, null, null);
    }

    /**
     * 根据专业获取成绩统计
     */
    public GradeStatisticsResponse getStatisticsByMajor(String major) {
        return getGradeStatistics(null, null, major, null, null);
    }

    /**
     * 根据学期获取成绩统计
     */
    public GradeStatisticsResponse getStatisticsBySemester(String semester) {
        return getGradeStatistics(null, null, null, semester, null);
    }

    /**
     * 根据年级获取成绩统计
     */
    public GradeStatisticsResponse getStatisticsByGrade(Integer gradeLevel) {
        return getGradeStatistics(null, null, null, null, gradeLevel);
    }

    /**
     * 筛选成绩数据
     */
    private List<Grade> filterGrades(
            List<Grade> grades,
            Long courseId,
            String className,
            String major,
            String semester,
            Integer gradeLevel) {

        return grades.stream()
                .filter(grade -> {
                    // 按课程筛选
                    if (courseId != null && !grade.getCourse().getId().equals(courseId)) {
                        return false;
                    }

                    // 按班级筛选
                    if (className != null && !className.isEmpty()) {
                        String studentClass = grade.getStudent().getClassName();
                        if (studentClass == null || !studentClass.equals(className)) {
                            return false;
                        }
                    }

                    // 按专业筛选
                    if (major != null && !major.isEmpty()) {
                        String studentMajor = grade.getStudent().getMajor();
                        if (studentMajor == null || !studentMajor.equals(major)) {
                            return false;
                        }
                    }

                    // 按学期筛选
                    if (semester != null && !semester.isEmpty()) {
                        String courseSemester = grade.getCourse().getSemester();
                        if (courseSemester == null || !courseSemester.equals(semester)) {
                            return false;
                        }
                    }

                    // 按年级筛选
                    if (gradeLevel != null) {
                        Integer studentGrade = grade.getStudent().getGradeLevel();
                        if (studentGrade == null || !studentGrade.equals(gradeLevel)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算统计数据
     */
    private GradeStatisticsResponse calculateStatistics(
            List<Grade> grades,
            Long courseId,
            String className,
            String major,
            String semester,
            Integer gradeLevel) {

        // 提取所有分数
        List<Double> scores = grades.stream()
                .map(g -> g.getScore().doubleValue())
                .sorted()
                .collect(Collectors.toList());

        int total = scores.size();

        // 计算基础统计
        double average = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double median = calculateMedian(scores);
        double stdDev = calculateStdDeviation(scores, average);

        // 获取最高分和最低分学生姓名
        String maxScoreStudent = grades.stream()
                .filter(g -> g.getScore().doubleValue() == max)
                .findFirst()
                .map(g -> g.getStudent().getName())
                .orElse("未知");

        String minScoreStudent = grades.stream()
                .filter(g -> g.getScore().doubleValue() == min)
                .findFirst()
                .map(g -> g.getStudent().getName())
                .orElse("未知");

        // 计算分数段人数
        int excellentCount = (int) scores.stream().filter(s -> s >= 90).count();
        int goodCount = (int) scores.stream().filter(s -> s >= 80 && s < 90).count();
        int averageCount = (int) scores.stream().filter(s -> s >= 70 && s < 80).count();
        int passCount = (int) scores.stream().filter(s -> s >= 60).count();
        int failCount = total - passCount;

        // 计算比率
        double excellentRate = total > 0 ? (excellentCount * 100.0 / total) : 0.0;
        double goodRate = total > 0 ? (goodCount * 100.0 / total) : 0.0;
        double averageRate = total > 0 ? (averageCount * 100.0 / total) : 0.0;
        double passRate = total > 0 ? (passCount * 100.0 / total) : 0.0;
        double failRate = total > 0 ? (failCount * 100.0 / total) : 0.0;

        // 构建分数分布
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("90-100", excellentCount);
        distribution.put("80-89", goodCount);
        distribution.put("70-79", averageCount);
        distribution.put("60-69", passCount - excellentCount - goodCount - averageCount);
        distribution.put("0-59", failCount);

        // 确定统计维度
        String dimension = determineDimension(courseId, className, major, semester, gradeLevel);
        String dimensionValue = determineDimensionValue(courseId, className, major, semester, gradeLevel);

        // 获取课程名称(如果按课程统计)
        String courseName = null;
        if (courseId != null) {
            courseName = courseRepository.findById(courseId)
                    .map(Course::getName)
                    .orElse("未知课程");
        }

        return GradeStatisticsResponse.builder()
                .dimension(dimension)
                .dimensionValue(dimensionValue)
                .dimensionId(courseId)
                .totalStudents(total)
                .averageScore(Math.round(average * 100.0) / 100.0)
                .maxScore(max)
                .maxScoreStudentName(maxScoreStudent)
                .minScore(min)
                .minScoreStudentName(minScoreStudent)
                .median(Math.round(median * 100.0) / 100.0)
                .stdDeviation(Math.round(stdDev * 100.0) / 100.0)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .excellentRate(Math.round(excellentRate * 100.0) / 100.0)
                .goodRate(Math.round(goodRate * 100.0) / 100.0)
                .averageRate(Math.round(averageRate * 100.0) / 100.0)
                .failRate(Math.round(failRate * 100.0) / 100.0)
                .passCount(passCount)
                .excellentCount(excellentCount)
                .goodCount(goodCount)
                .averageCount(averageCount)
                .failCount(failCount)
                .scoreDistribution(distribution)
                .courseName(courseName)
                .className(className)
                .major(major)
                .semester(semester)
                .gradeLevel(gradeLevel)
                .build();
    }

    /**
     * 计算中位数
     */
    private double calculateMedian(List<Double> sortedScores) {
        int size = sortedScores.size();
        if (size == 0)
            return 0.0;

        if (size % 2 == 0) {
            return (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        } else {
            return sortedScores.get(size / 2);
        }
    }

    /**
     * 计算标准差
     */
    private double calculateStdDeviation(List<Double> scores, double mean) {
        if (scores.size() <= 1)
            return 0.0;

        double variance = scores.stream()
                .mapToDouble(score -> Math.pow(score - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * 确定统计维度
     */
    private String determineDimension(Long courseId, String className, String major, String semester,
            Integer gradeLevel) {
        if (courseId != null)
            return "course";
        if (className != null && !className.isEmpty())
            return "class";
        if (major != null && !major.isEmpty())
            return "major";
        if (semester != null && !semester.isEmpty())
            return "semester";
        if (gradeLevel != null)
            return "grade";
        return "overall";
    }

    /**
     * 确定维度值
     */
    private String determineDimensionValue(Long courseId, String className, String major, String semester,
            Integer gradeLevel) {
        if (courseId != null) {
            return courseRepository.findById(courseId)
                    .map(Course::getName)
                    .orElse("未知课程");
        }
        if (className != null && !className.isEmpty())
            return className;
        if (major != null && !major.isEmpty())
            return major;
        if (semester != null && !semester.isEmpty())
            return semester;
        if (gradeLevel != null)
            return gradeLevel.toString() + "级";
        return "全部";
    }

    /**
     * 创建空统计结果
     */
    private GradeStatisticsResponse createEmptyStatistics() {
        return GradeStatisticsResponse.builder()
                .dimension("none")
                .dimensionValue("无数据")
                .totalStudents(0)
                .averageScore(0.0)
                .maxScore(0.0)
                .minScore(0.0)
                .median(0.0)
                .stdDeviation(0.0)
                .passRate(0.0)
                .excellentRate(0.0)
                .goodRate(0.0)
                .averageRate(0.0)
                .failRate(0.0)
                .passCount(0)
                .excellentCount(0)
                .goodCount(0)
                .averageCount(0)
                .failCount(0)
                .scoreDistribution(new LinkedHashMap<>())
                .build();
    }
}