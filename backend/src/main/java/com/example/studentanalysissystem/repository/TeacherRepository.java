package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师Repository接口
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * 根据工号查找教师
     */
    Optional<Teacher> findByEmployeeNumber(String employeeNumber);

    /**
     * 根据用户ID查找教师
     */
    Optional<Teacher> findByUserId(Long userId);

    /**
     * 检查工号是否存在
     */
    Boolean existsByEmployeeNumber(String employeeNumber);

    /**
     * 根据部门查找教师
     */
    List<Teacher> findByDepartment(String department);

    /**
     * 根据职称查找教师
     */
    List<Teacher> findByTitle(String title);

    /**
     * 根据部门和职称查找教师
     */
    List<Teacher> findByDepartmentAndTitle(String department, String title);

    /**
     * 根据姓名模糊查询
     */
    @Query("SELECT t FROM Teacher t WHERE t.name LIKE %:name%")
    List<Teacher> searchByName(@Param("name") String name);

    /**
     * 查询某个部门的教师数量
     */
    Long countByDepartment(String department);

    /**
     * 根据工号或姓名模糊查询
     */
    @Query("SELECT t FROM Teacher t WHERE t.employeeNumber LIKE %:keyword% OR t.name LIKE %:keyword%")
    List<Teacher> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 查询有课程的教师列表
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.courses c WHERE c.status = 'ACTIVE'")
    List<Teacher> findTeachersWithActiveCourses();
}