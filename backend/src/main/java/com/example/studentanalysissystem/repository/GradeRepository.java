package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 成绩Repository接口
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

        /**
         * 根据学生ID查找成绩
         */
        List<Grade> findByStudentId(Long studentId);

        /**
         * 根据课程ID查找成绩
         */
        List<Grade> findByCourseId(Long courseId);

        /**
         * 根据学生ID和课程ID查找成绩
         */
        List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);

        /**
         * 根据学生对象和课程对象查找成绩
         */
        Optional<Grade> findByStudentAndCourse(com.example.studentanalysissystem.model.Student student,
                        com.example.studentanalysissystem.model.Course course);

        /**
         * 根据学生ID、课程ID和考试类型查找成绩
         */
        Optional<Grade> findByStudentIdAndCourseIdAndExamType(Long studentId, Long courseId, String examType);

        /**
         * 根据考试类型查找成绩
         */
        List<Grade> findByExamType(String examType);

        /**
         * 根据等级查找成绩
         */
        List<Grade> findByGradeLevel(String gradeLevel);

        /**
         * 查询某学生的平均分
         */
        @Query("SELECT AVG(g.score) FROM Grade g WHERE g.student.id = :studentId")
        BigDecimal calculateAverageScoreByStudent(@Param("studentId") Long studentId);

        /**
         * 查询某课程的平均分
         */
        @Query("SELECT AVG(g.score) FROM Grade g WHERE g.course.id = :courseId")
        BigDecimal calculateAverageScoreByCourse(@Param("courseId") Long courseId);

        /**
         * 查询某学生某课程的平均分
         */
        @Query("SELECT AVG(g.score) FROM Grade g WHERE g.student.id = :studentId AND g.course.id = :courseId")
        BigDecimal calculateAverageScore(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

        /**
         * 查询某学生的最高分
         */
        @Query("SELECT MAX(g.score) FROM Grade g WHERE g.student.id = :studentId")
        BigDecimal findMaxScoreByStudent(@Param("studentId") Long studentId);

        /**
         * 查询某学生的最低分
         */
        @Query("SELECT MIN(g.score) FROM Grade g WHERE g.student.id = :studentId")
        BigDecimal findMinScoreByStudent(@Param("studentId") Long studentId);

        /**
         * 查询某课程的成绩分布
         */
        @Query("SELECT g.gradeLevel, COUNT(g) FROM Grade g WHERE g.course.id = :courseId GROUP BY g.gradeLevel")
        List<Object[]> findGradeDistributionByCourse(@Param("courseId") Long courseId);

        /**
         * 查询某学生在某日期范围内的成绩
         */
        @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.examDate BETWEEN :startDate AND :endDate")
        List<Grade> findByStudentAndDateRange(@Param("studentId") Long studentId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 查询不及格的成绩
         */
        @Query("SELECT g FROM Grade g WHERE g.gradeLevel = 'F'")
        List<Grade> findFailingGrades();

        /**
         * 统计某学生的成绩数量
         */
        Long countByStudentId(Long studentId);

        /**
         * 统计某课程的成绩数量
         */
        Long countByCourseId(Long courseId);

        /**
         * 查询某学生某学期的成绩
         */
        @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.course.semester = :semester")
        List<Grade> findByStudentAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

        /**
         * 根据课程ID和学生ID列表查找成绩
         */
        @Query("SELECT g FROM Grade g WHERE g.course.id = :courseId AND g.student.id IN :studentIds")
        List<Grade> findByCourseIdAndStudentIdIn(@Param("courseId") Long courseId,
                        @Param("studentIds") List<Long> studentIds);
}
