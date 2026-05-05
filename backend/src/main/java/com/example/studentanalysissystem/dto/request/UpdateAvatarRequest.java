package com.example.studentanalysissystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新头像请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAvatarRequest {

    @NotBlank(message = "头像URL不能为空")
    private String avatarUrl;
}
