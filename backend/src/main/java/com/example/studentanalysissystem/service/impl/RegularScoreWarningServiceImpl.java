package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.model.*;
import com.example.studentanalysissystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * 平时分预警服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RegularScoreWarningServiceImpl {

    private final StudentWarningRepository studentWarningRepository;
    private final GradeRepository gradeRepository;
    private final CourseDetailedWeightConfigRepository courseDetailedWeightConfigRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    /**
     * 为指定课程生成平时分预警
     */
    @Transactional
    public List<Map<String, Object>> generateRegularScoreWarnings(Long courseId, Long teacherId) {
        log.info("为课程{}生成平时分预警，教师ID: {}", courseId, teacherId);

        List<Map<String, Object>> warnings = new ArrayList<>();

        try {
            // 获取课程信息
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("课程不存在: " + courseId));

            // 获取教师信息
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("教师不存在: " + teacherId));

            // 检查是否已有未处理的预警，如果有则先删除，确保使用最新的班级平均分重新生成
            List<StudentWarning> existingWarnings = studentWarningRepository
                    .findByCourseIdAndIsProcessedFalseOrderByCreatedAtDesc(courseId);
            
            if (!existingWarnings.isEmpty()) {
                log.info("课程{}已有{}个未处理预警，删除旧预警并重新生成，确保班级平均分一致", courseId, existingWarnings.size());
                // 删除旧的未处理预警，确保重新生成时使用统一的班级平均分
                for (StudentWarning warning : existingWarnings) {
                    studentWarningRepository.delete(warning);
                }
                log.info("已删除课程{}的{}个旧预警", courseId, existingWarnings.size());
            }

            // 获取课程权重配置（优先使用详细配置，如果没有则使用普通配置）
            CourseDetailedWeightConfig detailedWeightConfig = courseDetailedWeightConfigRepository
                    .findByCourseIdAndIsActive(courseId, true)
                    .orElse(null);

            if (detailedWeightConfig == null) {
                log.warn("课程{}没有详细权重配置，无法生成预警", courseId);
                return warnings;
            }

            // 获取选修该课程的所有学生
            List<Long> studentIds = courseEnrollmentRepository.findStudentIdsByCourseId(courseId);
            List<Student> students = studentRepository.findAllById(studentIds);

            if (students.isEmpty()) {
                log.warn("课程{}没有选课学生", courseId);
                return warnings;
            }

            // 计算每个学生的平时分
            Map<Long, BigDecimal> studentRegularScores = new HashMap<>();
            for (Student student : students) {
                BigDecimal regularScore = calculateStudentRegularScore(student.getId(), courseId, detailedWeightConfig);
                studentRegularScores.put(student.getId(), regularScore);
            }

            // 计算班级平均分
            BigDecimal classAverage = calculateClassAverage(studentRegularScores);

            // 生成预警
            for (Student student : students) {
                BigDecimal studentScore = studentRegularScores.get(student.getId());
                if (studentScore == null) continue;

                String warningLevel = determineWarningLevel(studentScore, classAverage);
                if (warningLevel != null) {
                    // 创建预警
                    StudentWarning warning = createWarning(student, course, teacher, studentScore, classAverage, warningLevel);
                    studentWarningRepository.save(warning);

                    // 添加到返回列表
                    Map<String, Object> warningInfo = createWarningInfo(warning);
                    warnings.add(warningInfo);
                }
            }

            log.info("为课程{}生成了{}个预警", courseId, warnings.size());
            return warnings;

        } catch (Exception e) {
            log.error("生成平时分预警失败: {}", e.getMessage(), e);
            return warnings;
        }
    }

    /**
     * 动态计算课程的当前班级平均分
     */
    public BigDecimal calculateCurrentCourseAverage(Long courseId) {
        try {
            // 获取课程权重配置
            CourseDetailedWeightConfig detailedWeightConfig = courseDetailedWeightConfigRepository
                    .findByCourseIdAndIsActive(courseId, true)
                    .orElse(null);

            if (detailedWeightConfig == null) {
                log.warn("课程{}没有详细权重配置，无法计算班级平均分", courseId);
                return BigDecimal.ZERO;
            }

            // 获取选修该课程的所有学生
            List<Long> studentIds = courseEnrollmentRepository.findStudentIdsByCourseId(courseId);
            List<Student> students = studentRepository.findAllById(studentIds);

            if (students.isEmpty()) {
                log.warn("课程{}没有选课学生", courseId);
                return BigDecimal.ZERO;
            }

            // 计算每个学生的平时分
            Map<Long, BigDecimal> studentRegularScores = new HashMap<>();
            for (Student student : students) {
                BigDecimal regularScore = calculateStudentRegularScore(student.getId(), courseId, detailedWeightConfig);
                studentRegularScores.put(student.getId(), regularScore);
            }

            // 计算班级平均分
            return calculateClassAverage(studentRegularScores);
        } catch (Exception e) {
            log.error("计算课程{}的班级平均分失败: {}", courseId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 动态计算学生的当前平时分
     */
    public BigDecimal calculateCurrentStudentRegularScore(Long studentId, Long courseId) {
        try {
            // 获取课程权重配置
            CourseDetailedWeightConfig detailedWeightConfig = courseDetailedWeightConfigRepository
                    .findByCourseIdAndIsActive(courseId, true)
                    .orElse(null);

            if (detailedWeightConfig == null) {
                log.warn("课程{}没有详细权重配置，无法计算学生平时分", courseId);
                return BigDecimal.ZERO;
            }

            return calculateStudentRegularScore(studentId, courseId, detailedWeightConfig);
        } catch (Exception e) {
            log.error("计算学生{}在课程{}的平时分失败: {}", studentId, courseId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算学生平时分
     */
    private BigDecimal calculateStudentRegularScore(Long studentId, Long courseId, CourseDetailedWeightConfig weightConfig) {
        List<Grade> grades = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        Map<String, BigDecimal> regularScores = extractRegularScores(grades);

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 计算各种平时分的加权分数
        if (regularScores.containsKey("ATTENDANCE") && weightConfig.getAttendanceWeight() != null) {
            BigDecimal score = regularScores.get("ATTENDANCE");
            BigDecimal weight = weightConfig.getAttendanceWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (regularScores.containsKey("HOMEWORK") && weightConfig.getHomeworkWeight() != null) {
            BigDecimal score = regularScores.get("HOMEWORK");
            BigDecimal weight = weightConfig.getHomeworkWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (regularScores.containsKey("LAB") && weightConfig.getLabWeight() != null) {
            BigDecimal score = regularScores.get("LAB");
            BigDecimal weight = weightConfig.getLabWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        if (regularScores.containsKey("QUIZ") && weightConfig.getQuizWeight() != null) {
            BigDecimal score = regularScores.get("QUIZ");
            BigDecimal weight = weightConfig.getQuizWeight();
            BigDecimal weightedScore = score.multiply(weight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalScore = totalScore.add(weightedScore);
            totalWeight = totalWeight.add(weight);
        }

        // 如果权重总和不为100%，按比例调整
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            return totalScore.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 提取平时分成绩
     */
    private Map<String, BigDecimal> extractRegularScores(List<Grade> grades) {
        Map<String, BigDecimal> regularScores = new HashMap<>();

        for (Grade grade : grades) {
            String examType = grade.getExamType();
            // 只处理平时分类型
            if ("ATTENDANCE".equals(examType) || "HOMEWORK".equals(examType) || 
                "LAB".equals(examType) || "QUIZ".equals(examType)) {
                
                if (regularScores.containsKey(examType)) {
                    // 如果有多个相同类型的成绩，取平均值
                    BigDecimal existingScore = regularScores.get(examType);
                    BigDecimal newScore = existingScore.add(grade.getScore()).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    regularScores.put(examType, newScore);
                } else {
                    regularScores.put(examType, grade.getScore());
                }
            }
        }

        return regularScores;
    }

    /**
     * 计算班级平均分
     */
    private BigDecimal calculateClassAverage(Map<Long, BigDecimal> studentScores) {
        if (studentScores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = studentScores.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(studentScores.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 确定预警等级 - 新的预警策略
     */
    private String determineWarningLevel(BigDecimal studentScore, BigDecimal classAverage) {
        // 严重预警：平时分 < 60分
        if (studentScore.compareTo(BigDecimal.valueOf(60)) < 0) {
            return "SEVERE";
        }
        
        // 一般预警：60分 ≤ 平时分 < 70分
        if (studentScore.compareTo(BigDecimal.valueOf(60)) >= 0 && 
            studentScore.compareTo(BigDecimal.valueOf(70)) < 0) {
            return "GENERAL";
        }
        
        // 关注预警：70分 ≤ 平时分 < 80分 且 低于班级平均分20%
        if (studentScore.compareTo(BigDecimal.valueOf(70)) >= 0 && 
            studentScore.compareTo(BigDecimal.valueOf(80)) < 0) {
            BigDecimal relativeThreshold = classAverage.multiply(BigDecimal.valueOf(0.8));
            if (studentScore.compareTo(relativeThreshold) < 0) {
                return "ATTENTION";
            }
        }

        return null; // 不需要预警
    }

    /**
     * 创建预警记录 - 新的预警策略
     */
    private StudentWarning createWarning(Student student, Course course, Teacher teacher, 
                                       BigDecimal studentScore, BigDecimal classAverage, String warningLevel) {
        
        String title;
        String content;
        BigDecimal threshold;
        
        switch (warningLevel) {
            case "SEVERE":
                title = "平时分严重偏低警告";
                content = String.format("学生%s在课程%s中的平时分为%.2f分，严重低于60分阈值，请立即关注！",
                        student.getName(), course.getName(), studentScore);
                threshold = BigDecimal.valueOf(60);
                break;
            case "GENERAL":
                title = "平时分偏低提醒";
                content = String.format("学生%s在课程%s中的平时分为%.2f分，低于70分，建议关注。",
                        student.getName(), course.getName(), studentScore);
                threshold = BigDecimal.valueOf(70);
                break;
            case "ATTENTION":
                title = "平时分关注提醒";
                content = String.format("学生%s在课程%s中的平时分为%.2f分，低于班级平均分%.2f的80%阈值%.2f分，需要关注。",
                        student.getName(), course.getName(), studentScore, classAverage, classAverage.multiply(BigDecimal.valueOf(0.8)));
                threshold = classAverage.multiply(BigDecimal.valueOf(0.8));
                break;
            default:
                title = "平时分预警";
                content = String.format("学生%s在课程%s中的平时分为%.2f分，需要关注。",
                        student.getName(), course.getName(), studentScore);
                threshold = BigDecimal.valueOf(60);
        }

        return StudentWarning.builder()
                .student(student)
                .course(course)
                .teacher(teacher)
                .warningType("LOW_REGULAR_SCORE")
                .warningLevel(warningLevel)
                .title(title)
                .content(content)
                .currentRegularScore(studentScore)
                .classAverageScore(classAverage)
                .warningThreshold(threshold)
                .semester("2024Spring")
                .academicYear("2023-2024")
                .build();
    }

    /**
     * 创建预警信息
     */
    private Map<String, Object> createWarningInfo(StudentWarning warning) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", warning.getId());
        info.put("studentName", warning.getStudent().getName());
        info.put("studentNumber", warning.getStudent().getStudentNumber());
        info.put("courseName", warning.getCourse().getName());
        info.put("warningLevel", warning.getWarningLevel());
        info.put("title", warning.getTitle());
        info.put("content", warning.getContent());
        info.put("currentRegularScore", warning.getCurrentRegularScore());
        info.put("classAverageScore", warning.getClassAverageScore());
        info.put("warningThreshold", warning.getWarningThreshold());
        info.put("createdAt", warning.getCreatedAt());
        return info;
    }

    /**
     * 获取教师未处理的预警
     */
    public List<StudentWarning> getUnprocessedWarnings(Long teacherId) {
        log.info("查询教师ID {} 的未处理预警", teacherId);
        List<StudentWarning> warnings = studentWarningRepository.findUnprocessedWarningsByTeacher(teacherId);
        log.info("找到 {} 个未处理预警", warnings.size());
        return warnings;
    }

    /**
     * 获取学生未处理的预警
     */
    public List<StudentWarning> getStudentUnprocessedWarnings(Long studentId) {
        return studentWarningRepository.findByStudentIdAndIsProcessedFalseOrderByCreatedAtDesc(studentId);
    }

    /**
     * 标记预警为已处理
     */
    @Transactional
    public void markWarningAsProcessed(Long warningId, String processType, String remark) {
        StudentWarning warning = studentWarningRepository.findById(warningId)
                .orElseThrow(() -> new RuntimeException("预警不存在: " + warningId));
        
        warning.setIsProcessed(true);
        warning.setProcessType(processType);
        warning.setProcessRemark(remark);
        
        studentWarningRepository.save(warning);
    }

    /**
     * 删除预警
     */
    @Transactional
    public void deleteWarning(Long warningId) {
        studentWarningRepository.deleteById(warningId);
    }

    /**
     * 获取所有预警
     */
    public List<Map<String, Object>> getAllWarnings() {
        List<StudentWarning> warnings = studentWarningRepository.findAll();
        return warnings.stream()
            .map(this::convertToMap)
            .toList();
    }

    private Map<String, Object> convertToMap(StudentWarning warning) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", warning.getId());
        map.put("title", warning.getTitle());
        map.put("content", warning.getContent());
        map.put("warningType", warning.getWarningType());
        map.put("warningLevel", warning.getWarningLevel());
        map.put("warningThreshold", warning.getWarningThreshold());
        map.put("currentRegularScore", warning.getCurrentRegularScore());
        map.put("classAverageScore", warning.getClassAverageScore());
        map.put("courseId", warning.getCourse() != null ? warning.getCourse().getId() : null);
        map.put("studentId", warning.getStudent() != null ? warning.getStudent().getId() : null);
        map.put("teacherId", warning.getTeacher() != null ? warning.getTeacher().getId() : null);
        map.put("academicYear", warning.getAcademicYear());
        map.put("semester", warning.getSemester());
        map.put("isHandled", warning.getHandledAt() != null);
        map.put("isProcessed", warning.getIsProcessed());
        map.put("createdAt", warning.getCreatedAt());
        map.put("updatedAt", warning.getUpdatedAt());
        return map;
    }
}
