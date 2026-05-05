package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.CourseDetailedWeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 课程详细权重配置Repository接口
 */
@Repository
public interface CourseDetailedWeightConfigRepository extends JpaRepository<CourseDetailedWeightConfig, Long> {

    /**
     * 根据课程ID查找详细权重配置
     */
    Optional<CourseDetailedWeightConfig> findByCourseId(Long courseId);

    /**
     * 根据课程ID查找启用的详细权重配置
     */
    Optional<CourseDetailedWeightConfig> findByCourseIdAndIsActive(Long courseId, Boolean isActive);

    /**
     * 检查课程是否存在
     */
    @Query("SELECT COUNT(c.id) FROM Course c WHERE c.id = :courseId")
    int checkCourseExists(@Param("courseId") Long courseId);

    /**
     * 使用原生SQL创建或更新详细权重配置
     */
    @Query(value = """
        INSERT INTO course_detailed_weight_configs 
        (course_id, attendance_weight, homework_weight, lab_weight, quiz_weight, midterm_weight, final_weight, makeup_weight, is_active, description, created_at, updated_at)
        VALUES (:courseId, :attendanceWeight, :homeworkWeight, :labWeight, :quizWeight, :midtermWeight, :finalWeight, :makeupWeight, true, :description, NOW(), NOW())
        ON DUPLICATE KEY UPDATE
        attendance_weight = VALUES(attendance_weight),
        homework_weight = VALUES(homework_weight),
        lab_weight = VALUES(lab_weight),
        quiz_weight = VALUES(quiz_weight),
        midterm_weight = VALUES(midterm_weight),
        final_weight = VALUES(final_weight),
        makeup_weight = VALUES(makeup_weight),
        description = VALUES(description),
        updated_at = NOW()
        """, nativeQuery = true)
    int upsertDetailedWeightConfig(
            @Param("courseId") Long courseId,
            @Param("attendanceWeight") Double attendanceWeight,
            @Param("homeworkWeight") Double homeworkWeight,
            @Param("labWeight") Double labWeight,
            @Param("quizWeight") Double quizWeight,
            @Param("midtermWeight") Double midtermWeight,
            @Param("finalWeight") Double finalWeight,
            @Param("makeupWeight") Double makeupWeight,
            @Param("description") String description
    );

    /**
     * 使用原生SQL获取课程的详细权重配置
     */
    @Query(value = """
        SELECT 
            cdc.id,
            cdc.course_id,
            cdc.attendance_weight,
            cdc.homework_weight,
            cdc.lab_weight,
            cdc.quiz_weight,
            cdc.midterm_weight,
            cdc.final_weight,
            cdc.makeup_weight,
            cdc.description,
            cdc.is_active
        FROM course_detailed_weight_configs cdc
        WHERE cdc.course_id = :courseId AND cdc.is_active = true
        """, nativeQuery = true)
    java.util.Map<String, Object> findDetailedWeightConfigByCourseIdNative(@Param("courseId") Long courseId);

    /**
     * 获取课程详细权重配置（返回可修改的Map）
     * 使用@Query注解重写，避免TupleBackedMap问题
     */
    @Query(value = """
        SELECT 
            cdc.id,
            cdc.course_id,
            cdc.attendance_weight,
            cdc.homework_weight,
            cdc.lab_weight,
            cdc.quiz_weight,
            cdc.midterm_weight,
            cdc.final_weight,
            cdc.makeup_weight,
            cdc.description,
            cdc.is_active
        FROM course_detailed_weight_configs cdc
        WHERE cdc.course_id = :courseId AND cdc.is_active = true
        """, nativeQuery = true)
    java.util.List<java.util.Map<String, Object>> findDetailedWeightConfigByCourseIdList(@Param("courseId") Long courseId);

    /**
     * 获取课程详细权重配置（返回可修改的Map）
     */
    default java.util.Map<String, Object> getDetailedWeightConfigByCourseId(Long courseId) {
        java.util.List<java.util.Map<String, Object>> results = findDetailedWeightConfigByCourseIdList(courseId);
        if (results != null && !results.isEmpty()) {
            // 取第一个结果并创建新的可修改HashMap
            return new java.util.HashMap<>(results.get(0));
        }
        return null;
    }
}
