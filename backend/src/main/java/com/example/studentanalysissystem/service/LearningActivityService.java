package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.LearningActivityResponse;
import com.example.studentanalysissystem.dto.response.StudentActivityStatsResponse;
import com.example.studentanalysissystem.model.Course;
import com.example.studentanalysissystem.model.LearningActivity;
import com.example.studentanalysissystem.model.LearningActivity.ActivityType;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.repository.LearningActivityRepository;
import com.example.studentanalysissystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习活动服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningActivityService {

    private final LearningActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * 记录学习活动
     */
    @Transactional
    public LearningActivity recordActivity(Long studentId, Long courseId,
            ActivityType activityType,
            Map<String, Object> activityData,
            Integer duration) {
        LearningActivity activity = LearningActivity.builder()
                .studentId(studentId)
                .courseId(courseId)
                .activityType(activityType)
                .activityData(activityData)
                .duration(duration != null ? duration : 0)
                .build();

        LearningActivity saved = activityRepository.save(activity);
        log.info("记录学习活动: studentId={}, type={}, courseId={}",
                studentId, activityType, courseId);
        return saved;
    }

    /**
     * 获取学生活动统计
     */
    public StudentActivityStatsResponse getStudentActivityStats(Long studentId) {
        // 获取学生信息
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 统计总学习时长
        Integer totalStudyTime = activityRepository.sumDurationByStudentId(studentId);

        // 统计登录次数
        Long loginCount = activityRepository.countLoginsByStudentId(studentId);

        // 获取最近20条活动记录
        List<LearningActivity> recentActivities = activityRepository
                .findTop20ByStudentIdOrderByCreatedAtDesc(studentId);

        List<LearningActivityResponse> recentActivityResponses = recentActivities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 获取最近7天的活动统计
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> dailyStats = activityRepository.findDailyActivityCount(studentId, sevenDaysAgo);

        List<StudentActivityStatsResponse.DailyActivityStat> dailyStatList = dailyStats.stream()
                .map(row -> StudentActivityStatsResponse.DailyActivityStat.builder()
                        .date(row[0].toString())
                        .activityCount(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());

        // 总活动次数
        Long totalActivities = activityRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().count();

        return StudentActivityStatsResponse.builder()
                .studentId(studentId)
                .studentName(student.getName())
                .totalStudyTime(totalStudyTime)
                .loginCount(loginCount)
                .totalActivities(totalActivities)
                .recentActivities(recentActivityResponses)
                .dailyStats(dailyStatList)
                .build();
    }

    /**
     * 获取学生活动列表
     */
    public List<LearningActivityResponse> getStudentActivities(Long studentId, Integer limit) {
        List<LearningActivity> activities;
        if (limit != null && limit > 0) {
            activities = activityRepository.findTop20ByStudentIdOrderByCreatedAtDesc(studentId);
            activities = activities.stream().limit(limit).collect(Collectors.toList());
        } else {
            activities = activityRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        }

        return activities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取课程活动列表
     */
    public List<LearningActivityResponse> getCourseActivities(Long courseId) {
        List<LearningActivity> activities = activityRepository.findByCourseIdOrderByCreatedAtDesc(courseId);

        return activities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     */
    public LearningActivityResponse convertToResponse(LearningActivity activity) {
        String studentName = null;
        if (activity.getStudentId() != null) {
            studentRepository.findById(activity.getStudentId())
                    .ifPresent(student -> {
                    });
        }

        String courseName = null;
        if (activity.getCourseId() != null) {
            courseName = courseRepository.findById(activity.getCourseId())
                    .map(Course::getName)
                    .orElse(null);
        }

        return LearningActivityResponse.builder()
                .id(activity.getId())
                .studentId(activity.getStudentId())
                .studentName(studentName)
                .courseId(activity.getCourseId())
                .courseName(courseName)
                .activityType(activity.getActivityType())
                .activityTypeDesc(activity.getActivityType().getDescription())
                .activityData(activity.getActivityData())
                .duration(activity.getDuration())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    /**
     * 批量记录登录活动
     */
    @Transactional
    public void recordLogin(Long studentId) {
        recordActivity(studentId, null, ActivityType.LOGIN, null, 0);
    }

    /**
     * 记录查看资料活动
     */
    @Transactional
    public void recordViewMaterial(Long studentId, Long courseId, String materialTitle) {
        Map<String, Object> data = Map.of("title", materialTitle);
        recordActivity(studentId, courseId, ActivityType.VIEW_MATERIAL, data, 5);
    }

    /**
     * 记录提交作业活动
     */
    @Transactional
    public void recordSubmitAssignment(Long studentId, Long courseId, String assignmentTitle) {
        Map<String, Object> data = Map.of("title", assignmentTitle);
        recordActivity(studentId, courseId, ActivityType.SUBMIT_ASSIGNMENT, data, 30);
    }
}
