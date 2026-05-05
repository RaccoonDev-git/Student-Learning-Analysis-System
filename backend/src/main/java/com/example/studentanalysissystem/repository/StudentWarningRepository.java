package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.StudentWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生预警Repository接口
 */
@Repository
public interface StudentWarningRepository extends JpaRepository<StudentWarning, Long> {

    /**
     * 查找所有未处理的预警
     */
    List<StudentWarning> findByIsProcessedFalseOrderByCreatedAtDesc();

    /**
     * 根据教师ID查找未处理的预警
     */
    List<StudentWarning> findByTeacherIdAndIsProcessedFalseOrderByCreatedAtDesc(Long teacherId);
    
    /**
     * 使用自定义查询根据教师ID查找未处理的预警
     */
    @Query("SELECT w FROM StudentWarning w WHERE w.teacher.id = :teacherId AND w.isProcessed = false ORDER BY w.createdAt DESC")
    List<StudentWarning> findUnprocessedWarningsByTeacher(@Param("teacherId") Long teacherId);

    /**
     * 根据学生ID查找预警
     */
    List<StudentWarning> findByStudentIdAndIsProcessedFalseOrderByCreatedAtDesc(Long studentId);

    /**
     * 根据课程ID查找预警
     */
    List<StudentWarning> findByCourseIdAndIsProcessedFalseOrderByCreatedAtDesc(Long courseId);

    /**
     * 根据预警等级查找
     */
    List<StudentWarning> findByWarningLevelAndIsProcessedFalseOrderByCreatedAtDesc(String warningLevel);

    /**
     * 根据预警类型查找
     */
    List<StudentWarning> findByWarningTypeAndIsProcessedFalseOrderByCreatedAtDesc(String warningType);

    /**
     * 查找指定课程和学生的预警
     */
    List<StudentWarning> findByCourseIdAndStudentIdAndIsProcessedFalseOrderByCreatedAtDesc(Long courseId, Long studentId);

    /**
     * 查找指定课程和学生的所有预警（包括已处理的）
     */
    List<StudentWarning> findByCourseIdAndStudentIdOrderByCreatedAtDesc(Long courseId, Long studentId);

    /**
     * 统计教师未处理的预警数量
     */
    @Query("SELECT COUNT(w) FROM StudentWarning w WHERE w.teacher.id = :teacherId AND w.isProcessed = false")
    long countUnprocessedWarningsByTeacher(@Param("teacherId") Long teacherId);

    /**
     * 统计学生未处理的预警数量
     */
    @Query("SELECT COUNT(w) FROM StudentWarning w WHERE w.student.id = :studentId AND w.isProcessed = false")
    long countUnprocessedWarningsByStudent(@Param("studentId") Long studentId);

    /**
     * 删除旧的已处理预警（保留最近30天的）
     */
    @Query("DELETE FROM StudentWarning w WHERE w.isProcessed = true AND w.updatedAt < :cutoffDate")
    int deleteOldProcessedWarnings(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}