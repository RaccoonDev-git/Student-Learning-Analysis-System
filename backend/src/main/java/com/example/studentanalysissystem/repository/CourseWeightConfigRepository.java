package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.CourseWeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseWeightConfigRepository extends JpaRepository<CourseWeightConfig, Long> {
    
    /**
     * 根据课程ID查找权重配置
     */
    Optional<CourseWeightConfig> findByCourseIdAndIsActiveTrue(Long courseId);
    
    /**
     * 根据课程ID查找权重配置（包括不活跃的）
     */
    Optional<CourseWeightConfig> findByCourseId(Long courseId);
    
    /**
     * 检查课程是否有权重配置
     */
    boolean existsByCourseId(Long courseId);
    
    /**
     * 删除课程的权重配置
     */
    void deleteByCourseId(Long courseId);
    
    /**
     * 获取课程的默认权重配置（如果不存在则返回默认值）
     */
    @Query("SELECT cwc FROM CourseWeightConfig cwc WHERE cwc.courseId = :courseId AND cwc.isActive = true")
    Optional<CourseWeightConfig> findActiveByCourseId(@Param("courseId") Long courseId);
}