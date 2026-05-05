package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.LearningActivity;
import com.example.studentanalysissystem.model.LearningActivity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习活动数据访问接口
 */
@Repository
public interface LearningActivityRepository extends JpaRepository<LearningActivity, Long> {

        /**
         * 根据学生ID查找活动记录
         */
        List<LearningActivity> findByStudentIdOrderByCreatedAtDesc(Long studentId);

        /**
         * 根据学生ID和活动类型查找
         */
        List<LearningActivity> findByStudentIdAndActivityTypeOrderByCreatedAtDesc(
                        Long studentId, ActivityType activityType);

        /**
         * 根据学生ID和时间范围查找
         */
        List<LearningActivity> findByStudentIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        Long studentId, LocalDateTime startTime, LocalDateTime endTime);

        /**
         * 统计学生总学习时长(分钟)
         */
        @Query("SELECT COALESCE(SUM(la.duration), 0) FROM LearningActivity la WHERE la.studentId = :studentId")
        Integer sumDurationByStudentId(@Param("studentId") Long studentId);

        /**
         * 统计学生登录次数
         */
        @Query("SELECT COUNT(la) FROM LearningActivity la WHERE la.studentId = :studentId AND la.activityType = 'LOGIN'")
        Long countLoginsByStudentId(@Param("studentId") Long studentId);

        /**
         * 获取学生最近的活动记录
         */
        List<LearningActivity> findTop20ByStudentIdOrderByCreatedAtDesc(Long studentId);

        /**
         * 根据课程ID查找活动记录
         */
        List<LearningActivity> findByCourseIdOrderByCreatedAtDesc(Long courseId);

        /**
         * 统计指定课程的学生活跃度
         */
        @Query("SELECT la.studentId, COUNT(la) as activityCount FROM LearningActivity la " +
                        "WHERE la.courseId = :courseId GROUP BY la.studentId ORDER BY activityCount DESC")
        List<Object[]> findStudentActivityCountByCourseId(@Param("courseId") Long courseId);

        /**
         * 获取最近N天的活动统计
         */
        @Query("SELECT DATE(la.createdAt) as date, COUNT(la) as count " +
                        "FROM LearningActivity la " +
                        "WHERE la.studentId = :studentId AND la.createdAt >= :startDate " +
                        "GROUP BY DATE(la.createdAt) ORDER BY date DESC")
        List<Object[]> findDailyActivityCount(
                        @Param("studentId") Long studentId,
                        @Param("startDate") LocalDateTime startDate);

        /**
         * 获取学生最近一条活动记录
         */
        java.util.Optional<LearningActivity> findTop1ByStudentIdOrderByCreatedAtDesc(Long studentId);

        /**
         * 根据学生ID和创建时间查找活动（用于预警系统）
         */
        List<LearningActivity> findByStudentIdAndCreatedAtAfter(Long studentId, java.time.LocalDateTime createdAt);
}
