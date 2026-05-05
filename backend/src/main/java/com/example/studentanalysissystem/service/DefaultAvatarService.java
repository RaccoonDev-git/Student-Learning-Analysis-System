package com.example.studentanalysissystem.service;

import org.springframework.stereotype.Service;

/**
 * 默认头像服务
 * 管理系统中所有用户的默认头像
 */
@Service
public class DefaultAvatarService {
    
    // 默认头像URL - 使用用户提供的自定义默认头像
    private static final String DEFAULT_AVATAR_URL = "/api/static/default-avatar.jpg";
    
    /**
     * 获取默认头像URL
     */
    public String getDefaultAvatarUrl() {
        return DEFAULT_AVATAR_URL;
    }
    
    /**
     * 生成基于用户名的默认头像URL
     * 可以使用Gravatar或其他头像生成服务
     */
    public String generateAvatarUrl(String username, String email) {
        if (email != null && !email.isEmpty()) {
            // 使用Gravatar生成头像
            String hash = generateMD5Hash(email.toLowerCase().trim());
            return "https://www.gravatar.com/avatar/" + hash + "?d=identicon&s=200";
        } else {
            // 使用用户名生成头像
            return "https://ui-avatars.com/api/?name=" + username + "&size=200&background=random";
        }
    }
    
    /**
     * 简单的MD5哈希生成（实际项目中应使用Apache Commons Codec）
     */
    private String generateMD5Hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "default";
        }
    }
    
    /**
     * 检查是否为默认头像
     */
    public boolean isDefaultAvatar(String avatarUrl) {
        return avatarUrl == null || 
               avatarUrl.isEmpty() || 
               avatarUrl.equals(DEFAULT_AVATAR_URL) ||
               avatarUrl.startsWith("https://ui-avatars.com/") ||
               avatarUrl.startsWith("https://www.gravatar.com/");
    }
}
