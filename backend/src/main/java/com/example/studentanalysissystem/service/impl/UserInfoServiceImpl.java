package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.response.UserInfoResponse;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.model.Teacher;
import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.repository.StudentRepository;
import com.example.studentanalysissystem.repository.TeacherRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public List<Long> getValidTeacherIds() {
        return teacherRepository.findAll()
                .stream()
                .filter(teacher -> teacher.getUser() != null)
                .map(teacher -> teacher.getUser().getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getValidStudentIds() {
        return studentRepository.findAll()
                .stream()
                .filter(student -> student.getUser() != null)
                .map(student -> student.getUser().getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserInfoResponse> getAllUserBasicInfo() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserInfoResponse.UserInfoResponseBuilder builder = UserInfoResponse.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .name(user.getUsername()) // 默认使用用户名作为姓名
                            .role(user.getRole().toString());

                    // 根据角色获取头像信息
                    if (user.getRole() == User.UserRole.STUDENT) {
                        studentRepository.findByUserId(user.getId()).ifPresent(student -> {
                            builder.name(student.getName())
                                   .avatarUrl(student.getAvatarUrl())
                                   .hasCustomAvatar(student.getHasCustomAvatar());
                        });
                    } else if (user.getRole() == User.UserRole.TEACHER) {
                        teacherRepository.findByUserId(user.getId()).ifPresent(teacher -> {
                            builder.name(teacher.getName())
                                   .avatarUrl(teacher.getAvatarUrl())
                                   .hasCustomAvatar(teacher.getHasCustomAvatar());
                        });
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }
}
