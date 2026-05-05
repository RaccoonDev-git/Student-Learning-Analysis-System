package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.response.ResourceResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教学资源服务接口
 */
public interface ResourceService {

    /**
     * 上传资源
     */
    ResourceResponse uploadResource(MultipartFile file,
            String name,
            String description,
            Long uploaderId,
            String uploaderName,
            Long courseId,
            String category);

    /**
     * 下载资源
     */
    Resource downloadResource(Long id);

    /**
     * 获取资源详情
     */
    ResourceResponse getResourceById(Long id);

    /**
     * 获取所有资源
     */
    List<ResourceResponse> getAllResources();

    /**
     * 根据类型筛选资源
     */
    List<ResourceResponse> getResourcesByType(String fileType);

    /**
     * 根据上传者查询资源
     */
    List<ResourceResponse> getResourcesByUploader(Long uploaderId);

    /**
     * 根据课程查询资源
     */
    List<ResourceResponse> getResourcesByCourse(Long courseId);

    /**
     * 搜索资源
     */
    List<ResourceResponse> searchResources(String keyword);

    /**
     * 综合筛选资源
     */
    List<ResourceResponse> filterResources(String fileType, String category, Long uploaderId);

    /**
     * 删除资源
     */
    void deleteResource(Long id, Long userId);

    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);
}
