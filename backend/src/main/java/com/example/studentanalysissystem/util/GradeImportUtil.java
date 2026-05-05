package com.example.studentanalysissystem.util;

import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.model.Teacher;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 成绩导入工具类
 * 用于批量导入学生成绩数据
 */
@Slf4j
@Component
public class GradeImportUtil {

    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final TeacherRepository teacherRepository;

    // 课程名称缓存，避免重复查询数据库
    private final Map<String, Course> courseCache = new HashMap<>();

    public GradeImportUtil(CourseRepository courseRepository,
            GradeRepository gradeRepository,
            TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * 从导入的行数据中提取并保存成绩
     * 
     * @param student 学生对象
     * @param rowData 行数据Map
     * @return 导入的成绩数量
     */
    public int importGradesFromRowData(Student student, Map<String, String> rowData) {
        int importedCount = 0;

        // 提取所有成绩相关字段
        Map<String, BigDecimal> courseScores = extractCourseScores(rowData);

        if (courseScores.isEmpty()) {
            log.debug("学生 {} 没有成绩数据", student.getStudentNumber());
            return 0;
        }

        // 为每门课程创建成绩记录
        for (Map.Entry<String, BigDecimal> entry : courseScores.entrySet()) {
            String courseName = entry.getKey();
            BigDecimal score = entry.getValue();

            try {
                // 获取或创建课程
                Course course = getOrCreateCourse(courseName);

                // 检查是否已存在成绩记录
                Optional<Grade> existingGrade = gradeRepository
                        .findByStudentAndCourse(student, course);

                if (existingGrade.isPresent()) {
                    // 更新现有成绩
                    Grade grade = existingGrade.get();
                    grade.setScore(score);
                    grade.setTotalScore(BigDecimal.valueOf(100)); // 默认满分100
                    grade.setExamDate(LocalDate.now());
                    gradeRepository.save(grade);
                    log.debug("更新成绩: 学生={}, 课程={}, 分数={}",
                            student.getStudentNumber(), courseName, score);
                } else {
                    // 创建新成绩记录
                    Grade grade = Grade.builder()
                            .student(student)
                            .course(course)
                            .score(score)
                            .totalScore(BigDecimal.valueOf(100))
                            .examType("期末考试")
                            .examDate(LocalDate.now())
                            .build();

                    gradeRepository.save(grade);
                    log.debug("创建成绩: 学生={}, 课程={}, 分数={}",
                            student.getStudentNumber(), courseName, score);
                }

                importedCount++;
            } catch (Exception e) {
                log.error("导入成绩失败: 学生={}, 课程={}, 错误={}",
                        student.getStudentNumber(), courseName, e.getMessage());
            }
        }

        return importedCount;
    }

    /**
     * 从行数据中提取课程成绩
     * 支持的格式：
     * 1. "课程名称-成绩" 如 "数学-90"
     * 2. 独立的课程列和成绩列，如 "课程1"="数学", "成绩1"="90"
     * 3. 直接的课程名称作为列名，如 "数学"="90"
     */
    private Map<String, BigDecimal> extractCourseScores(Map<String, String> rowData) {
        Map<String, BigDecimal> courseScores = new HashMap<>();

        // 方法1: 查找格式为 "课程1", "课程2" 配对 "成绩1", "成绩2" 的字段
        for (int i = 1; i <= 20; i++) { // 最多支持20门课程
            String courseKey = "课程" + i;
            String scoreKey = "成绩" + i;

            if (rowData.containsKey(courseKey) && rowData.containsKey(scoreKey)) {
                String courseName = rowData.get(courseKey);
                String scoreStr = rowData.get(scoreKey);

                if (isValidCourseName(courseName) && isValidScore(scoreStr)) {
                    try {
                        BigDecimal score = new BigDecimal(scoreStr.trim());
                        courseScores.put(courseName.trim(), score);
                    } catch (NumberFormatException e) {
                        log.warn("无效的成绩格式: {}", scoreStr);
                    }
                }
            }
        }

        // 方法2: 查找课程名称作为列名的字段
        // 排除基本信息字段
        Set<String> excludeFields = Set.of("姓名", "学号", "年级", "班级", "专业",
                "手机号", "联系方式", "备注", "入学日期", "毕业日期");

        for (Map.Entry<String, String> entry : rowData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // 跳过基本信息字段和已处理的课程字段
            if (excludeFields.contains(key) || key.startsWith("课程") || key.startsWith("成绩")) {
                continue;
            }

            // 如果字段值是有效的分数，则认为字段名是课程名
            if (isValidScore(value)) {
                try {
                    BigDecimal score = new BigDecimal(value.trim());
                    courseScores.put(key.trim(), score);
                } catch (NumberFormatException e) {
                    // 忽略无效格式
                }
            }
        }

        return courseScores;
    }

    /**
     * 验证课程名称是否有效
     */
    private boolean isValidCourseName(String courseName) {
        return courseName != null &&
                !courseName.trim().isEmpty() &&
                courseName.trim().length() >= 2 &&
                courseName.trim().length() <= 100;
    }

    /**
     * 验证分数是否有效
     */
    private boolean isValidScore(String scoreStr) {
        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            return false;
        }

        try {
            BigDecimal score = new BigDecimal(scoreStr.trim());
            return score.compareTo(BigDecimal.ZERO) >= 0 &&
                    score.compareTo(BigDecimal.valueOf(150)) <= 0; // 支持0-150分
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取或创建课程
     * 如果课程不存在，则自动创建
     */
    private Course getOrCreateCourse(String courseName) {
        // 先从缓存中查找
        if (courseCache.containsKey(courseName)) {
            return courseCache.get(courseName);
        }

        // 从数据库查找
        Optional<Course> existingCourse = courseRepository.findByName(courseName);
        if (existingCourse.isPresent()) {
            courseCache.put(courseName, existingCourse.get());
            return existingCourse.get();
        }

        // 创建新课程
        // 获取一个默认教师（系统管理员或第一个教师）
        Teacher defaultTeacher = teacherRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("系统中没有教师，无法创建课程"));

        // 生成课程代码
        String courseCode = generateCourseCode(courseName);

        Course newCourse = Course.builder()
                .name(courseName)
                .code(courseCode)
                .teacher(defaultTeacher)
                .description("通过批量导入自动创建")
                .credits(3) // 默认3学分
                .semester("2024-2025学年第一学期")
                .academicYear("2024-2025")
                .maxStudents(100) // 默认容量100人
                .status(Course.CourseStatus.ACTIVE)
                .build();

        Course savedCourse = courseRepository.save(newCourse);
        courseCache.put(courseName, savedCourse);

        log.info("自动创建课程: {}, 代码: {}", courseName, courseCode);

        return savedCourse;
    }

    /**
     * 生成课程代码
     * 基于课程名称生成唯一的课程代码
     */
    private String generateCourseCode(String courseName) {
        // 获取课程名称的拼音首字母或前几个字符
        String baseCode = courseName.length() > 6
                ? courseName.substring(0, 6)
                : courseName;

        // 添加随机后缀确保唯一性
        String code = baseCode + "_" + System.currentTimeMillis() % 10000;

        // 检查是否已存在
        int suffix = 1;
        String finalCode = code;
        while (courseRepository.findByCode(finalCode).isPresent()) {
            finalCode = code + "_" + suffix++;
        }

        return finalCode;
    }

    /**
     * 清空课程缓存
     */
    public void clearCourseCache() {
        courseCache.clear();
    }

    /**
     * 批量导入成绩
     * 
     * @param students    学生列表
     * @param rowDataList 对应的行数据列表
     * @return 导入的成绩总数
     */
    public Map<String, Object> batchImportGrades(List<Student> students, List<Map<String, String>> rowDataList) {
        int totalGrades = 0;
        int totalCourses = 0;
        Set<String> coursesCreated = new HashSet<>();

        if (students.size() != rowDataList.size()) {
            throw new IllegalArgumentException("学生列表和数据行列表大小不匹配");
        }

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            Map<String, String> rowData = rowDataList.get(i);

            int gradesImported = importGradesFromRowData(student, rowData);
            totalGrades += gradesImported;
        }

        totalCourses = courseCache.size();
        coursesCreated.addAll(courseCache.keySet());

        Map<String, Object> result = new HashMap<>();
        result.put("totalGrades", totalGrades);
        result.put("totalCourses", totalCourses);
        result.put("coursesCreated", coursesCreated);

        return result;
    }
}
