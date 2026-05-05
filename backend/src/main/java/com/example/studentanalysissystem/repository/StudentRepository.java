package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生Repository接口
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

        /**
         * 根据学号查找学生
         */
        Optional<Student> findByStudentNumber(String studentNumber);

        /**
         * 根据用户ID查找学生
         */
        Optional<Student> findByUserId(Long userId);

        /**
         * 检查学号是否存在
         */
        boolean existsByStudentNumber(String studentNumber);

        /**
         * 根据班级查找学生
         */
        List<Student> findByClassName(String className);

        /**
         * 根据年级查找学生
         */
        List<Student> findByGradeLevel(Integer gradeLevel);

        /**
         * 根据专业查找学生
         */
        List<Student> findByMajor(String major);

        /**
         * 根据课程ID查找学生（通过选课关系）
         */
        @Query("SELECT DISTINCT s FROM Student s JOIN s.courseEnrollments ce WHERE ce.course.id = :courseId")
        List<Student> findByCourseId(@Param("courseId") Long courseId);

        /**
         * 根据班级和年级查找学生
         */
        List<Student> findByClassNameAndGradeLevel(String className, Integer gradeLevel);

        /**
         * 根据姓名模糊查询
         */
        @Query("SELECT s FROM Student s WHERE s.name LIKE %:name%")
        List<Student> searchByName(@Param("name") String name);

        /**
         * 查询某个班级的学生数量
         */
        @Query("SELECT COUNT(s) FROM Student s WHERE s.className = :className")
        Long countByClassName(@Param("className") String className);

        /**
         * 查询某个年级的学生数量
         */
        Long countByGradeLevel(Integer gradeLevel);

        /**
         * 根据学号或姓名模糊查询
         */
        @Query("SELECT s FROM Student s WHERE s.studentNumber LIKE %:keyword% OR s.name LIKE %:keyword%")
        List<Student> searchByKeyword(@Param("keyword") String keyword);

        /**
         * 高级筛选学生
         */
        @Query("SELECT s FROM Student s WHERE " +
                        "(:gradeLevel IS NULL OR s.gradeLevel = :gradeLevel) AND " +
                        "(:className IS NULL OR s.className = :className) AND " +
                        "(:major IS NULL OR s.major = :major) AND " +
                        "(:keyword IS NULL OR s.name LIKE %:keyword% OR s.studentNumber LIKE %:keyword%)")
        List<Student> filterStudents(
                        @Param("gradeLevel") Integer gradeLevel,
                        @Param("className") String className,
                        @Param("major") String major,
                        @Param("keyword") String keyword);
}
