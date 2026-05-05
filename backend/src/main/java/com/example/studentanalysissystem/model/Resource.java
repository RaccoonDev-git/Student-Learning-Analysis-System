package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教学资源实体类
 */
@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 资源名称

    @Column(nullable = false)
    private String originalFilename; // 原始文件名

    @Column(nullable = false)
    private String fileType; // 文件类型(pdf, doc, ppt, video等)

    @Column(nullable = false)
    private String filePath; // 文件存储路径

    private Long fileSize; // 文件大小(字节)

    private String description; // 资源描述

    @Column(nullable = false)
    private Long uploaderId; // 上传者ID

    private String uploaderName; // 上传者姓名

    private Long courseId; // 关联课程ID(可选)

    private String category; // 资源分类(课件、作业、试卷、素材等)

    private Integer downloadCount; // 下载次数

    @Column(nullable = false)
    private LocalDateTime uploadTime; // 上传时间

    private LocalDateTime updateTime; // 更新时间

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive; // 是否有效

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
        isActive = true;
        downloadCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
