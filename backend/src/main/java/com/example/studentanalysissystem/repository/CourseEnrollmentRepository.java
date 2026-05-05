package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 选课Repository接口
 */
@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /**
     * 根据学生ID查找选课记录
     */
    List<CourseEnrollment> findByStudentId(Long studentId);

    /**
     * 根据课程ID查找选课记录
     */
    List<CourseEnrollment> findByCourseId(Long courseId);

    /**
     * 根据学生ID和课程ID查找选课记录
     */
    Optional<CourseEnrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 检查学生是否已选某课程
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 根据学生ID和状态查找选课记录
     */
    List<CourseEnrollment> findByStudentIdAndStatus(Long studentId, CourseEnrollment.EnrollmentStatus status);

    /**
     * 根据课程ID和状态查找选课记录
     */
    List<CourseEnrollment> findByCourseIdAndStatus(Long courseId, CourseEnrollment.EnrollmentStatus status);

    /**
     * 统计某课程的选课人数
     */
    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.id = :courseId AND e.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId,
            @Param("status") CourseEnrollment.EnrollmentStatus status);

    /**
     * 统计某学生的选课数量
     */
    Long countByStudentIdAndStatus(Long studentId, CourseEnrollment.EnrollmentStatus status);

    /**
     * 查询某学生在某学期的选课
     */
    @Query("SELECT e FROM CourseEnrollment e WHERE e.student.id = :studentId AND e.course.semester = :semester")
    List<CourseEnrollment> findByStudentAndSemester(@Param("studentId") Long studentId,
            @Param("semester") String semester);

    /**
     * 查询某课程的所有已选学生
     */
    @Query("SELECT e FROM CourseEnrollment e JOIN FETCH e.student WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    List<CourseEnrollment> findEnrolledStudentsByCourse(@Param("courseId") Long courseId);

    /**
     * 根据课程ID获取选修该课程的学生ID列表
     */
    @Query("SELECT e.student.id FROM CourseEnrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    List<Long> findStudentIdsByCourseId(@Param("courseId") Long courseId);
}
