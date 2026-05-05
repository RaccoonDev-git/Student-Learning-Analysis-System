package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.CourseResponse;
import com.example.studentanalysissystem.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Course实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    /**
     * Course实体转CourseResponse DTO
     */
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.name", target = "teacherName")
    @Mapping(target = "enrolledCount", ignore = true)
    CourseResponse toResponse(Course course);

    /**
     * Course实体列表转CourseResponse DTO列表
     */
    List<CourseResponse> toResponseList(List<Course> courses);
}
