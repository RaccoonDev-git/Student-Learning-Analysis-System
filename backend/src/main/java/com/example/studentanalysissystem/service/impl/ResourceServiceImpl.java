package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.response.ResourceResponse;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import com.example.studentanalysissystem.model.User;
import com.example.studentanalysissystem.repository.ResourceRepository;
import com.example.studentanalysissystem.repository.UserRepository;
import com.example.studentanalysissystem.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 教学资源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ResourceResponse uploadResource(MultipartFile file,
            String name,
            String description,
            Long uploaderId,
            String uploaderName,
            Long courseId,
            String category) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                throw new IllegalArgumentException("文件不能为空");
            }

            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 确定文件类型
            String fileType = determineFileType(fileExtension);

            // 创建资源记录
            com.example.studentanalysissystem.model.Resource resource = com.example.studentanalysissystem.model.Resource
                    .builder()
                    .name(name != null && !name.isEmpty() ? name : originalFilename)
                    .originalFilename(originalFilename)
                    .fileType(fileType)
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .description(description)
                    .uploaderId(uploaderId)
                    .uploaderName(uploaderName)
                    .courseId(courseId)
                    .category(category)
                    .build();

            com.example.studentanalysissystem.model.Resource savedResource = resourceRepository.save(resource);

            log.info("资源上传成功: {}, 上传者: {}", originalFilename, uploaderName);

            return mapToResponse(savedResource);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Resource downloadResource(Long id) {
        try {
            com.example.studentanalysissystem.model.Resource resource = resourceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

            Path filePath = Paths.get(resource.getFilePath());
            Resource fileResource = new UrlResource(filePath.toUri());

            if (fileResource.exists() && fileResource.isReadable()) {
                return fileResource;
            } else {
                throw new RuntimeException("文件不存在或不可读");
            }
        } catch (MalformedURLException e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public ResourceResponse getResourceById(Long id) {
        com.example.studentanalysissystem.model.Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));
        return mapToResponse(resource);
    }

    @Override
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAllByIsActiveTrueOrderByUploadTimeDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByType(String fileType) {
        return resourceRepository.findByFileTypeAndIsActiveTrue(fileType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByUploader(Long uploaderId) {
        return resourceRepository.findByUploaderIdAndIsActiveTrue(uploaderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> getResourcesByCourse(Long courseId) {
        return resourceRepository.findByCourseIdAndIsActiveTrue(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> searchResources(String keyword) {
        return resourceRepository.searchResources(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceResponse> filterResources(String fileType, String category, Long uploaderId) {
        return resourceRepository.findByFilters(fileType, category, uploaderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteResource(Long id, Long userId) {
        com.example.studentanalysissystem.model.Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        // 验证权限(只有上传者或管理员可以删除)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!resource.getUploaderId().equals(userId) &&
                !user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("无权删除此资源");
        }

        // 软删除
        resource.setIsActive(false);
        resourceRepository.save(resource);

        // 可选:物理删除文件
        try {
            Path filePath = Paths.get(resource.getFilePath());
            Files.deleteIfExists(filePath);
            log.info("资源文件删除成功: {}", resource.getOriginalFilename());
        } catch (IOException e) {
            log.warn("删除物理文件失败: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        com.example.studentanalysissystem.model.Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        resource.setDownloadCount(resource.getDownloadCount() + 1);
        resourceRepository.save(resource);
    }

    // 辅助方法:根据文件扩展名确定文件类型
    private String determineFileType(String extension) {
        String ext = extension.toLowerCase();
        if (ext.equals(".pdf"))
            return "pdf";
        if (ext.matches("\\.(doc|docx)"))
            return "doc";
        if (ext.matches("\\.(ppt|pptx)"))
            return "ppt";
        if (ext.matches("\\.(xls|xlsx)"))
            return "excel";
        if (ext.matches("\\.(jpg|jpeg|png|gif|bmp)"))
            return "image";
        if (ext.matches("\\.(mp4|avi|mov|wmv)"))
            return "video";
        if (ext.matches("\\.(mp3|wav|wma)"))
            return "audio";
        if (ext.matches("\\.(zip|rar|7z)"))
            return "archive";
        return "other";
    }

    // 辅助方法:映射为响应DTO
    private ResourceResponse mapToResponse(com.example.studentanalysissystem.model.Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .originalFilename(resource.getOriginalFilename())
                .fileType(resource.getFileType())
                .fileSize(resource.getFileSize())
                .description(resource.getDescription())
                .uploaderId(resource.getUploaderId())
                .uploaderName(resource.getUploaderName())
                .courseId(resource.getCourseId())
                .category(resource.getCategory())
                .downloadCount(resource.getDownloadCount())
                .uploadTime(resource.getUploadTime())
                .updateTime(resource.getUpdateTime())
                .build();
    }
}
