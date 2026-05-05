package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程Repository接口
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 根据课程代码查找课程
     */
    Optional<Course> findByCode(String code);

    /**
     * 根据课程名称查找课程
     */
    Optional<Course> findByName(String name);

    /**
     * 检查课程代码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据教师ID查找课程
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * 根据学期查找课程
     */
    List<Course> findBySemester(String semester);

    /**
     * 根据学年查找课程
     */
    List<Course> findByAcademicYear(String academicYear);

    /**
     * 根据状态查找课程
     */
    List<Course> findByStatus(Course.CourseStatus status);

    /**
     * 根据教师ID和状态查找课程
     */
    List<Course> findByTeacherIdAndStatus(Long teacherId, Course.CourseStatus status);

    /**
     * 根据学期和状态查找课程
     */
    List<Course> findBySemesterAndStatus(String semester, Course.CourseStatus status);

    /**
     * 根据课程名称模糊查询
     */
    @Query("SELECT c FROM Course c WHERE c.name LIKE %:name%")
    List<Course> searchByName(@Param("name") String name);

    /**
     * 根据课程代码或名称模糊查询
     */
    @Query("SELECT c FROM Course c WHERE c.code LIKE %:keyword% OR c.name LIKE %:keyword%")
    List<Course> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 查询某教师在某学期的课程
     */
    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId AND c.semester = :semester")
    List<Course> findByTeacherAndSemester(@Param("teacherId") Long teacherId, @Param("semester") String semester);

    /**
     * 查询有选课学生的课程
     */
    @Query("SELECT DISTINCT c FROM Course c JOIN c.enrollments e WHERE e.status = 'ENROLLED'")
    List<Course> findCoursesWithEnrolledStudents();

    /**
     * 统计某教师的课程数量
     */
    Long countByTeacherId(Long teacherId);

    /**
     * 统计某学期的课程数量
     */
    Long countBySemester(String semester);
}
