package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 成绩类型Repository接口
 */
@Repository
public interface GradeTypeRepository extends JpaRepository<GradeType, Long> {

    /**
     * 根据类型代码查找成绩类型
     */
    Optional<GradeType> findByTypeCode(String typeCode);

    /**
     * 查找所有启用的成绩类型
     */
    List<GradeType> findByIsActiveTrueOrderBySortOrder();

    /**
     * 查找所有平时分类型
     */
    List<GradeType> findByIsRegularTrueAndIsActiveTrueOrderBySortOrder();

    /**
     * 查找所有期末分类型
     */
    List<GradeType> findByIsFinalTrueAndIsActiveTrueOrderBySortOrder();

    /**
     * 查找所有补考类型
     */
    List<GradeType> findByIsMakeupTrueAndIsActiveTrueOrderBySortOrder();

    /**
     * 根据类型代码列表查找成绩类型
     */
    @Query("SELECT gt FROM GradeType gt WHERE gt.typeCode IN :typeCodes AND gt.isActive = true ORDER BY gt.sortOrder")
    List<GradeType> findByTypeCodeInAndIsActiveTrueOrderBySortOrder(@Param("typeCodes") List<String> typeCodes);
}
