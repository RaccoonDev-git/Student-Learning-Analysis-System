package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.UserInfoResponse;

import java.util.List;

/**
 * 用户信息服务接口
 */
public interface UserInfoService {
    
    /**
     * 获取所有有效的教师ID列表
     */
    List<Long> getValidTeacherIds();
    
    /**
     * 获取所有有效的学生ID列表
     */
    List<Long> getValidStudentIds();
    
    /**
     * 获取所有用户的基本信息
     */
    List<UserInfoResponse> getAllUserBasicInfo();
}
