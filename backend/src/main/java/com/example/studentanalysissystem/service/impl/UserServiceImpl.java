package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.LoginRequest;
import com.example.studentanalysissystem.dto.request.RegisterRequest;
import com.example.studentanalysissystem.dto.request.UpdateAvatarRequest;
import com.example.studentanalysissystem.dto.request.UpdateUserRequest;
import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.exception.BusinessException;
import com.example.studentanalysissystem.exception.DuplicateResourceException;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.mapper.UserMapper;
import com.example.studentanalysissystem.model.Student;
import com.example.studentanalysissystem.model.Teacher;
import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.repository.StudentRepository;
import com.example.studentanalysissystem.repository.TeacherRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.service.UserService;
import com.example.studentanalysissystem.service.DefaultAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DefaultAvatarService defaultAvatarService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("User", "phone", request.getPhone());
        }

        // 创建用户实体
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt密码加密
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));

        // 使用BCrypt验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BusinessException("用户账号已被锁定或禁用");
        }

        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserResponse> getUsersByRole(User.UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return userMapper.toResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 检查邮箱是否被其他用户占用
        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // 检查手机号是否被其他用户占用
        if (request.getPhone() != null && !user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("User", "phone", request.getPhone());
        }

        // 更新用户信息
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, User.UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 使用BCrypt验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 使用BCrypt加密新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 管理员重置密码,不需要验证旧密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getRecentUsers(int limit) {
        List<User> users = userRepository.findAll();
        // 按创建时间倒序排序,取前limit个
        return users.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(limit)
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> searchUsers(String keyword) {
        String searchPattern = "%" + keyword.toLowerCase() + "%";
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword, keyword);
        return userMapper.toResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(Long id, UpdateAvatarRequest request) {
        // 查找用户
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 根据用户角色更新对应的头像字段
        if (user.getRole() == User.UserRole.STUDENT) {
            // 更新学生头像
            updateStudentAvatar(user, request.getAvatarUrl());
        } else if (user.getRole() == User.UserRole.TEACHER) {
            // 更新教师头像
            updateTeacherAvatar(user, request.getAvatarUrl());
        }

        // 返回更新后的用户信息
        return userMapper.toResponse(user);
    }

    private void updateStudentAvatar(User user, String avatarUrl) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId", user.getId()));
        
        // 检查是否为默认头像
        boolean isDefaultAvatar = defaultAvatarService.isDefaultAvatar(avatarUrl);
        student.setHasCustomAvatar(!isDefaultAvatar);
        
        if (isDefaultAvatar) {
            // 如果是默认头像，生成个性化头像
            String generatedAvatar = defaultAvatarService.generateAvatarUrl(user.getUsername(), user.getEmail());
            student.setAvatarUrl(generatedAvatar);
        } else {
            // 用户自定义头像
            student.setAvatarUrl(avatarUrl);
        }
        
        studentRepository.save(student);
    }

    private void updateTeacherAvatar(User user, String avatarUrl) {
        Teacher teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "userId", user.getId()));
        
        // 检查是否为默认头像
        boolean isDefaultAvatar = defaultAvatarService.isDefaultAvatar(avatarUrl);
        teacher.setHasCustomAvatar(!isDefaultAvatar);
        
        if (isDefaultAvatar) {
            // 如果是默认头像，生成个性化头像
            String generatedAvatar = defaultAvatarService.generateAvatarUrl(user.getUsername(), user.getEmail());
            teacher.setAvatarUrl(generatedAvatar);
        } else {
            // 用户自定义头像
            teacher.setAvatarUrl(avatarUrl);
        }
        
        teacherRepository.save(teacher);
    }
}