package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户基本信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String name;
    private String role;
    private String avatarUrl;
    private Boolean hasCustomAvatar;
}
