package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.TeacherResponse;
import com.example.studentanalysissystem.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Teacher实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    /**
     * Teacher实体转TeacherResponse DTO
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    TeacherResponse toResponse(Teacher teacher);

    /**
     * Teacher实体列表转TeacherResponse DTO列表
     */
    List<TeacherResponse> toResponseList(List<Teacher> teachers);
}
