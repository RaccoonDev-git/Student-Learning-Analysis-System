package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.EnrollmentResponse;
import com.example.studentanalysissystem.model.CourseEnrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * CourseEnrollment实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentMapper INSTANCE = Mappers.getMapper(EnrollmentMapper.class);

    /**
     * CourseEnrollment实体转EnrollmentResponse DTO
     */
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "student.studentNumber", target = "studentNumber")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.code", target = "courseCode")
    @Mapping(source = "course.name", target = "courseName")
    EnrollmentResponse toResponse(CourseEnrollment enrollment);

    /**
     * CourseEnrollment实体列表转EnrollmentResponse DTO列表
     */
    List<EnrollmentResponse> toResponseList(List<CourseEnrollment> enrollments);
}
