package com.example.studentanalysissystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponse {
    private Long id;
    private String name;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private String description;
    private Long uploaderId;
    private String uploaderName;
    private Long courseId;
    private String category;
    private Integer downloadCount;
    private LocalDateTime uploadTime;
    private LocalDateTime updateTime;
}
