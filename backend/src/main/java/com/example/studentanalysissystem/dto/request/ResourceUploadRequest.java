package com.example.studentanalysissystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源上传请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceUploadRequest {
    private String name;
    private String description;
    private Long courseId;
    private String category;
}
