package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.StudentResponse;
import com.example.studentanalysissystem.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Student实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    /**
     * Student实体转StudentResponse DTO
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    StudentResponse toResponse(Student student);

    /**
     * Student实体列表转StudentResponse DTO列表
     */
    List<StudentResponse> toResponseList(List<Student> students);
}
