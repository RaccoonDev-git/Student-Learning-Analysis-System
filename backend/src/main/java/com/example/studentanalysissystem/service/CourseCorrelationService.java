package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.CourseCorrelationResponse;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.Grade;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.GradeRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程相关性分析服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CourseCorrelationService {

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    /**
     * 分析两门课程成绩之间的相关性
     * 
     * @param courseId1 第一门课程ID
     * @param courseId2 第二门课程ID
     * @return 课程相关性分析结果
     */
    public CourseCorrelationResponse analyzeCourseCorrelation(Long courseId1, Long courseId2) {
        log.info("开始课程相关性分析: courseId1={}, courseId2={}", courseId1, courseId2);

        // 验证课程ID
        if (courseId1 == null || courseId2 == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }

        if (courseId1.equals(courseId2)) {
            throw new IllegalArgumentException("不能分析同一门课程的相关性");
        }

        // 获取课程信息
        Course course1 = courseRepository.findById(courseId1)
                .orElseThrow(() -> new RuntimeException("课程ID " + courseId1 + " 不存在"));
        Course course2 = courseRepository.findById(courseId2)
                .orElseThrow(() -> new RuntimeException("课程ID " + courseId2 + " 不存在"));

        // 获取两门课程的成绩
        List<Grade> grades1 = gradeRepository.findByCourseId(courseId1);
        List<Grade> grades2 = gradeRepository.findByCourseId(courseId2);

        if (grades1.isEmpty() || grades2.isEmpty()) {
            log.warn("课程成绩数据不足，无法进行相关性分析");
            return createEmptyResponse(course1, course2);
        }

        // 找出同时选修两门课程的学生
        Map<Long, Double> scores1Map = grades1.stream()
                .collect(Collectors.toMap(
                        g -> g.getStudent().getId(),
                        g -> g.getScore().doubleValue(),
                        (v1, v2) -> v1 // 如果有重复，保留第一个
                ));

        Map<Long, Double> scores2Map = grades2.stream()
                .collect(Collectors.toMap(
                        g -> g.getStudent().getId(),
                        g -> g.getScore().doubleValue(),
                        (v1, v2) -> v1));

        // 获取共同学生的成绩对
        List<CourseCorrelationResponse.ScatterPoint> scatterData = new ArrayList<>();
        List<Double> xScores = new ArrayList<>();
        List<Double> yScores = new ArrayList<>();

        for (Long studentId : scores1Map.keySet()) {
            if (scores2Map.containsKey(studentId)) {
                double score1 = scores1Map.get(studentId);
                double score2 = scores2Map.get(studentId);

                // 获取学生姓名
                String studentName = studentRepository.findById(studentId)
                        .map(s -> s.getName())
                        .orElse(null);

                scatterData.add(CourseCorrelationResponse.ScatterPoint.builder()
                        .course1Score(score1)
                        .course2Score(score2)
                        .studentName(studentName)
                        .build());

                xScores.add(score1);
                yScores.add(score2);
            }
        }

        if (scatterData.size() < 3) {
            log.warn("共同学生数量不足（< 3），无法计算可靠的相关性");
            return createInsufficientDataResponse(course1, course2, scatterData.size());
        }

        // 计算皮尔逊相关系数
        double correlationCoefficient = calculatePearsonCorrelation(xScores, yScores);

        // 判断相关性强度
        String correlationStrength = determineCorrelationStrength(correlationCoefficient);

        // 计算课程平均分
        double course1Average = xScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double course2Average = yScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // 构建课程信息
        CourseCorrelationResponse.CourseInfo courseInfo1 = CourseCorrelationResponse.CourseInfo.builder()
                .id(courseId1)
                .name(course1.getName())
                .averageScore(Math.round(course1Average * 100.0) / 100.0)
                .build();

        CourseCorrelationResponse.CourseInfo courseInfo2 = CourseCorrelationResponse.CourseInfo.builder()
                .id(courseId2)
                .name(course2.getName())
                .averageScore(Math.round(course2Average * 100.0) / 100.0)
                .build();

        return CourseCorrelationResponse.builder()
                .course1(courseInfo1)
                .course2(courseInfo2)
                .correlationCoefficient(Math.round(correlationCoefficient * 10000.0) / 10000.0)
                .correlationStrength(correlationStrength)
                .sampleSize(scatterData.size())
                .scatterData(scatterData)
                .build();
    }

    /**
     * 计算皮尔逊相关系数
     * r = Σ[(Xi - X̄)(Yi - Ȳ)] / √[Σ(Xi - X̄)² × Σ(Yi - Ȳ)²]
     */
    private double calculatePearsonCorrelation(List<Double> xScores, List<Double> yScores) {
        int n = xScores.size();
        if (n == 0)
            return 0.0;

        // 计算平均值
        double xMean = xScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double yMean = yScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // 计算协方差和标准差
        double covariance = 0.0;
        double xVariance = 0.0;
        double yVariance = 0.0;

        for (int i = 0; i < n; i++) {
            double xDiff = xScores.get(i) - xMean;
            double yDiff = yScores.get(i) - yMean;

            covariance += xDiff * yDiff;
            xVariance += xDiff * xDiff;
            yVariance += yDiff * yDiff;
        }

        // 避免除以零
        if (xVariance == 0.0 || yVariance == 0.0) {
            return 0.0;
        }

        return covariance / Math.sqrt(xVariance * yVariance);
    }

    /**
     * 判断相关性强度
     * |r| >= 0.8: 强相关
     * 0.5 <= |r| < 0.8: 中等相关
     * 0.3 <= |r| < 0.5: 弱相关
     * |r| < 0.3: 无相关或极弱相关
     */
    private String determineCorrelationStrength(double r) {
        double absR = Math.abs(r);

        if (absR >= 0.8) {
            return r > 0 ? "强正相关" : "强负相关";
        } else if (absR >= 0.5) {
            return r > 0 ? "中等正相关" : "中等负相关";
        } else if (absR >= 0.3) {
            return r > 0 ? "弱正相关" : "弱负相关";
        } else {
            return "无相关";
        }
    }

    /**
     * 生成相关性描述
     */
    private String generateCorrelationDescription(String course1Name, String course2Name,
            double r, String strength) {
        if (Math.abs(r) < 0.3) {
            return String.format("《%s》和《%s》的成绩之间没有明显的相关性（r=%.4f），" +
                    "说明这两门课程相对独立，学生在一门课程上的表现不能很好地预测其在另一门课程上的表现。",
                    course1Name, course2Name, r);
        }

        String direction = r > 0 ? "正" : "负";
        String interpretation;

        if (r > 0) {
            interpretation = String.format("这意味着在《%s》上表现好的学生，往往在《%s》上也表现较好；" +
                    "反之，在《%s》上表现较差的学生，在《%s》上的表现也相对较差。",
                    course1Name, course2Name, course1Name, course2Name);
        } else {
            interpretation = String.format("这意味着在《%s》上表现好的学生，在《%s》上可能表现较差；" +
                    "反之，在《%s》上表现较差的学生，在《%s》上可能表现较好。这种负相关关系比较少见。",
                    course1Name, course2Name, course1Name, course2Name);
        }

        return String.format("《%s》和《%s》的成绩之间呈现%s关系（r=%.4f），相关性为%s。%s",
                course1Name, course2Name, direction, r, strength, interpretation);
    }

    /**
     * 创建空响应
     */
    private CourseCorrelationResponse createEmptyResponse(Course course1, Course course2) {
        CourseCorrelationResponse.CourseInfo courseInfo1 = CourseCorrelationResponse.CourseInfo.builder()
                .id(course1.getId())
                .name(course1.getName())
                .averageScore(0.0)
                .build();

        CourseCorrelationResponse.CourseInfo courseInfo2 = CourseCorrelationResponse.CourseInfo.builder()
                .id(course2.getId())
                .name(course2.getName())
                .averageScore(0.0)
                .build();

        return CourseCorrelationResponse.builder()
                .course1(courseInfo1)
                .course2(courseInfo2)
                .correlationCoefficient(0.0)
                .correlationStrength("无数据")
                .sampleSize(0)
                .scatterData(new ArrayList<>())
                .build();
    }

    /**
     * 创建数据不足响应
     */
    private CourseCorrelationResponse createInsufficientDataResponse(Course course1, Course course2, int sampleSize) {
        CourseCorrelationResponse.CourseInfo courseInfo1 = CourseCorrelationResponse.CourseInfo.builder()
                .id(course1.getId())
                .name(course1.getName())
                .averageScore(0.0)
                .build();

        CourseCorrelationResponse.CourseInfo courseInfo2 = CourseCorrelationResponse.CourseInfo.builder()
                .id(course2.getId())
                .name(course2.getName())
                .averageScore(0.0)
                .build();

        return CourseCorrelationResponse.builder()
                .course1(courseInfo1)
                .course2(courseInfo2)
                .correlationCoefficient(0.0)
                .correlationStrength("数据不足")
                .sampleSize(sampleSize)
                .scatterData(new ArrayList<>())
                .build();
    }
}
