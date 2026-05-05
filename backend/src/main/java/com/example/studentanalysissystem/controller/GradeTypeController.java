package com.example.studentanalysissystem.controller;

import com.example.studentanalysissystem.model.GradeType;
import com.example.studentanalysissystem.repository.GradeTypeRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成绩类型控制器
 */
@RestController
@RequestMapping("/api/grade-types")
@RequiredArgsConstructor
@Tag(name = "成绩类型管理", description = "成绩类型的增删改查")
@CrossOrigin(origins = "*")
public class GradeTypeController {

    private final GradeTypeRepository gradeTypeRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取所有成绩类型", description = "获取所有启用的成绩类型")
    public ResponseEntity<List<GradeType>> getAll() {
        List<GradeType> types = gradeTypeRepository.findByIsActiveTrueOrderBySortOrder();
        return ResponseEntity.ok(types);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "创建成绩类型", description = "创建新的成绩类型")
    public ResponseEntity<GradeType> create(@RequestBody CreateGradeTypeRequest request) {
        GradeType gradeType = GradeType.builder()
                .typeCode(request.getTypeCode())
                .typeName(request.getTypeName())
                .isRegular(request.getIsRegular())
                .isFinal(request.getIsFinal())
                .isMakeup(request.getIsMakeup())
                .defaultWeight(request.getDefaultWeight())
                .fullScore(request.getFullScore())
                .sortOrder(request.getSortOrder())
                .description(request.getDescription())
                .isActive(true)
                .build();

        GradeType savedType = gradeTypeRepository.save(gradeType);
        return ResponseEntity.ok(savedType);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "批量创建成绩类型", description = "批量创建成绩类型")
    public ResponseEntity<List<GradeType>> batchCreate(@RequestBody FrontendGradeTypeRequest frontendRequest) {
        List<GradeType> gradeTypes = frontendRequest.getGradeTypes().stream()
                .map(request -> {
                    // 确保typeCode不为null，如果为null则使用typeName
                    String typeCode = request.getType();
                    if (typeCode == null || typeCode.trim().isEmpty()) {
                        typeCode = request.getName().toUpperCase().replace(" ", "_");
                    }

                    return GradeType.builder()
                            .typeCode(typeCode)
                            .typeName(request.getName())
                            .isRegular(true) // 默认都是平时分类型
                            .isFinal(false)
                            .isMakeup(false)
                            .defaultWeight(request.getDefaultWeight())
                            .fullScore(java.math.BigDecimal.valueOf(100.00))
                            .sortOrder(1)
                            .description(request.getDescription())
                            .isActive(true)
                            .build();
                })
                .toList();

        List<GradeType> savedTypes = gradeTypeRepository.saveAll(gradeTypes);

        // 注意：综合成绩功能已移除，不再需要重新计算

        return ResponseEntity.ok(savedTypes);
    }

    @GetMapping("/regular")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取平时分类型", description = "获取所有平时分类型")
    public ResponseEntity<List<GradeType>> getRegularTypes() {
        List<GradeType> types = gradeTypeRepository.findByIsRegularTrueAndIsActiveTrueOrderBySortOrder();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/final")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取期末分类型", description = "获取所有期末分类型")
    public ResponseEntity<List<GradeType>> getFinalTypes() {
        List<GradeType> types = gradeTypeRepository.findByIsFinalTrueAndIsActiveTrueOrderBySortOrder();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/makeup")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "获取补考类型", description = "获取所有补考类型")
    public ResponseEntity<List<GradeType>> getMakeupTypes() {
        List<GradeType> types = gradeTypeRepository.findByIsMakeupTrueAndIsActiveTrueOrderBySortOrder();
        return ResponseEntity.ok(types);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "更新成绩类型", description = "更新指定的成绩类型")
    public ResponseEntity<GradeType> update(@PathVariable Long id, @RequestBody CreateGradeTypeRequest request) {
        return gradeTypeRepository.findById(id)
                .map(type -> {
                    type.setTypeName(request.getTypeName());
                    type.setIsRegular(request.getIsRegular());
                    type.setIsFinal(request.getIsFinal());
                    type.setIsMakeup(request.getIsMakeup());
                    type.setDefaultWeight(request.getDefaultWeight());
                    type.setFullScore(request.getFullScore());
                    type.setSortOrder(request.getSortOrder());
                    type.setDescription(request.getDescription());
                    GradeType updatedType = gradeTypeRepository.save(type);

                    // 注意：综合成绩功能已移除，不再需要重新计算

                    return ResponseEntity.ok(updatedType);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除成绩类型", description = "删除指定的成绩类型")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!gradeTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 注意：综合成绩功能已移除，不再需要重新计算

        gradeTypeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 请求DTO
    public static class CreateGradeTypeRequest {
        private String typeCode;
        private String typeName;
        private Boolean isRegular = true;
        private Boolean isFinal = false;
        private Boolean isMakeup = false;
        private java.math.BigDecimal defaultWeight;
        private java.math.BigDecimal fullScore;
        private Integer sortOrder;
        private String description;

        // Getters and Setters
        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public Boolean getIsRegular() {
            return isRegular;
        }

        public void setIsRegular(Boolean isRegular) {
            this.isRegular = isRegular;
        }

        public Boolean getIsFinal() {
            return isFinal;
        }

        public void setIsFinal(Boolean isFinal) {
            this.isFinal = isFinal;
        }

        public Boolean getIsMakeup() {
            return isMakeup;
        }

        public void setIsMakeup(Boolean isMakeup) {
            this.isMakeup = isMakeup;
        }

        public java.math.BigDecimal getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(java.math.BigDecimal defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public java.math.BigDecimal getFullScore() {
            return fullScore;
        }

        public void setFullScore(java.math.BigDecimal fullScore) {
            this.fullScore = fullScore;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // 批量创建请求DTO
    public static class BatchCreateGradeTypeRequest {
        @JsonProperty("courseId")
        private Long courseId;

        @JsonProperty("gradeTypes")
        private List<CreateGradeTypeRequest> gradeTypes;

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public List<CreateGradeTypeRequest> getGradeTypes() {
            return gradeTypes;
        }

        public void setGradeTypes(List<CreateGradeTypeRequest> gradeTypes) {
            this.gradeTypes = gradeTypes;
        }
    }

    // 前端发送的成绩类型请求DTO
    public static class FrontendGradeTypeRequest {
        @JsonProperty("courseId")
        private Long courseId;

        @JsonProperty("gradeTypes")
        private List<FrontendGradeType> gradeTypes;

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public List<FrontendGradeType> getGradeTypes() {
            return gradeTypes;
        }

        public void setGradeTypes(List<FrontendGradeType> gradeTypes) {
            this.gradeTypes = gradeTypes;
        }
    }

    // 前端发送的单个成绩类型DTO
    public static class FrontendGradeType {
        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("defaultWeight")
        private java.math.BigDecimal defaultWeight;

        @JsonProperty("description")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public java.math.BigDecimal getDefaultWeight() {
            return defaultWeight;
        }

        public void setDefaultWeight(java.math.BigDecimal defaultWeight) {
            this.defaultWeight = defaultWeight;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
