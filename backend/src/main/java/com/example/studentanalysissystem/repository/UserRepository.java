package com.example.studentanalysissystem.repository;

import com.example.studentanalysissystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户Repository接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    Boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    Boolean existsByPhone(String phone);

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(User.UserRole role);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * 根据角色和状态查找用户
     */
    List<User> findByRoleAndStatus(User.UserRole role, User.UserStatus status);

    /**
     * 查找最近登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :since ORDER BY u.lastLogin DESC")
    List<User> findRecentlyLoggedInUsers(@Param("since") LocalDateTime since);

    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据用户名或邮箱模糊搜索(忽略大小写)
     */
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}