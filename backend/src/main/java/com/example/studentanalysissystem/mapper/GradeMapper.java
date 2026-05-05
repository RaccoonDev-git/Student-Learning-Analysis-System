package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.GradeResponse;
import com.example.studentanalysissystem.model.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Grade实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface GradeMapper {

    GradeMapper INSTANCE = Mappers.getMapper(GradeMapper.class);

    /**
     * Grade实体转GradeResponse DTO
     */
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "student.studentNumber", target = "studentNumber")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.code", target = "courseCode")
    @Mapping(source = "course.name", target = "courseName")
    GradeResponse toResponse(Grade grade);

    /**
     * Grade实体列表转GradeResponse DTO列表
     */
    List<GradeResponse> toResponseList(List<Grade> grades);
}
