package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教学资源数据访问接口
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /**
     * 根据文件类型查询资源
     */
    List<Resource> findByFileTypeAndIsActiveTrue(String fileType);

    /**
     * 根据上传者ID查询资源
     */
    List<Resource> findByUploaderIdAndIsActiveTrue(Long uploaderId);

    /**
     * 根据课程ID查询资源
     */
    List<Resource> findByCourseIdAndIsActiveTrue(Long courseId);

    /**
     * 根据分类查询资源
     */
    List<Resource> findByCategoryAndIsActiveTrue(String category);

    /**
     * 查询所有有效资源
     */
    List<Resource> findAllByIsActiveTrueOrderByUploadTimeDesc();

    /**
     * 搜索资源(按名称或描述)
     */
    @Query("SELECT r FROM Resource r WHERE r.isActive = true " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY r.uploadTime DESC")
    List<Resource> searchResources(@Param("keyword") String keyword);

    /**
     * 根据多个条件查询资源
     */
    @Query("SELECT r FROM Resource r WHERE r.isActive = true " +
            "AND (:fileType IS NULL OR r.fileType = :fileType) " +
            "AND (:category IS NULL OR r.category = :category) " +
            "AND (:uploaderId IS NULL OR r.uploaderId = :uploaderId) " +
            "ORDER BY r.uploadTime DESC")
    List<Resource> findByFilters(@Param("fileType") String fileType,
            @Param("category") String category,
            @Param("uploaderId") Long uploaderId);
}
