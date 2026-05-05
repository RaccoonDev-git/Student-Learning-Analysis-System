package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.LoginRequest;
import com.example.studentanalysissystem.dto.request.RegisterRequest;
import com.example.studentanalysissystem.dto.request.UpdateAvatarRequest;
import com.example.studentanalysissystem.dto.request.UpdateUserRequest;
import com.example.studentanalysissystem.dto.response.UserResponse;
import com.example.studentanalysissystem.model.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    UserResponse login(LoginRequest request);

    /**
     * 根据ID查询用户
     */
    UserResponse getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    UserResponse getUserByUsername(String username);

    /**
     * 查询所有用户
     */
    List<UserResponse> getAllUsers();

    /**
     * 查询所有用户(返回User实体)
     * 用于导出等需要完整User对象的场景
     */
    List<User> findAllUsers();

    /**
     * 根据角色查询用户
     */
    List<UserResponse> getUsersByRole(User.UserRole role);

    /**
     * 更新用户信息
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     */
    UserResponse updateUserStatus(Long id, User.UserStatus status);

    /**
     * 修改密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置密码(管理员操作)
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 获取最近注册的用户
     */
    List<UserResponse> getRecentUsers(int limit);

    /**
     * 搜索用户(根据用户名或邮箱)
     */
    List<UserResponse> searchUsers(String keyword);

    /**
     * 更新用户头像
     */
    UserResponse updateAvatar(Long id, UpdateAvatarRequest request);
}