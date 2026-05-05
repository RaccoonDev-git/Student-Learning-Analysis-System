package com.example.studentanalysissystem.mapper;

import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * User实体与DTO映射器
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * User实体转UserResponse DTO
     */
    UserResponse toResponse(User user);

    /**
     * User实体列表转UserResponse DTO列表
     */
    List<UserResponse> toResponseList(List<User> users);
}
