package com.example.studentanalysissystem.service.impl;

import com.example.studentanalysissystem.dto.request.WeightConfigRequest;
import com.example.studentanalysissystem.dto.response.WeightConfigResponse;
import com.example.studentanalysissystem.model.CourseWeightConfig;
import com.example.studentanalysissystem.service.WeightConfigService;
import com.example.studentanalysissystem.repository.CourseWeightConfigRepository;
import com.example.studentanalysissystem.repository.CourseRepository;
import com.example.studentanalysissystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WeightConfigServiceImpl implements WeightConfigService {

    private final CourseWeightConfigRepository courseWeightConfigRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public WeightConfigResponse getWeightConfigByCourseId(Long courseId) {
        log.info("获取课程{}的权重配置", courseId);
        
        // 获取权重配置
        CourseWeightConfig config = courseWeightConfigRepository
            .findByCourseIdAndIsActiveTrue(courseId)
            .orElse(null);
        
        if (config == null) {
            // 如果没有配置，返回默认配置
            return createDefaultConfig(courseId);
        }
        
        // 获取课程名称
        String courseName = courseRepository.findById(courseId)
            .map(course -> course.getName())
            .orElse("未知课程");
        
        return WeightConfigResponse.builder()
            .id(config.getId())
            .courseId(courseId)
            .courseName(courseName)
            .attendanceWeight(config.getAttendanceWeight())
            .homeworkWeight(config.getHomeworkWeight())
            .labWeight(config.getLabWeight())
            .quizWeight(config.getQuizWeight())
            .midtermWeight(config.getMidtermWeight())
            .isActive(config.getIsActive())
            .description(config.getDescription())
            .createdAt(config.getCreatedAt())
            .updatedAt(config.getUpdatedAt())
            .isDefault(false)
            .build();
    }

    @Override
    public WeightConfigResponse createOrUpdateWeightConfig(WeightConfigRequest request) {
        log.info("创建或更新权重配置: {}", request);
        
        // 验证权重总和
        if (!request.isValidWeightSum()) {
            throw new IllegalArgumentException("权重总和必须为100%");
        }
        
        // 查找现有配置
        CourseWeightConfig existingConfig = courseWeightConfigRepository
            .findByCourseId(request.getCourseId())
            .orElse(null);
        
        CourseWeightConfig config;
        if (existingConfig != null) {
            // 更新现有配置
            existingConfig.setAttendanceWeight(request.getAttendanceWeight());
            existingConfig.setHomeworkWeight(request.getHomeworkWeight());
            existingConfig.setLabWeight(request.getLabWeight());
            existingConfig.setQuizWeight(request.getQuizWeight());
            existingConfig.setMidtermWeight(request.getMidtermWeight());
            existingConfig.setDescription(request.getDescription());
            existingConfig.setIsActive(true);
            config = courseWeightConfigRepository.save(existingConfig);
        } else {
            // 创建新配置
            config = new CourseWeightConfig();
            config.setCourseId(request.getCourseId());
            config.setAttendanceWeight(request.getAttendanceWeight());
            config.setHomeworkWeight(request.getHomeworkWeight());
            config.setLabWeight(request.getLabWeight());
            config.setQuizWeight(request.getQuizWeight());
            config.setMidtermWeight(request.getMidtermWeight());
            config.setDescription(request.getDescription());
            config.setIsActive(true);
            config = courseWeightConfigRepository.save(config);
        }
        
        // 获取课程名称
        String courseName = courseRepository.findById(request.getCourseId())
            .map(course -> course.getName())
            .orElse("未知课程");
        
        return WeightConfigResponse.builder()
            .id(config.getId())
            .courseId(config.getCourseId())
            .courseName(courseName)
            .attendanceWeight(config.getAttendanceWeight())
            .homeworkWeight(config.getHomeworkWeight())
            .labWeight(config.getLabWeight())
            .quizWeight(config.getQuizWeight())
            .midtermWeight(config.getMidtermWeight())
            .isActive(config.getIsActive())
            .description(config.getDescription())
            .createdAt(config.getCreatedAt())
            .updatedAt(config.getUpdatedAt())
            .isDefault(false)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeightConfigResponse> getAllWeightConfigs() {
        log.info("获取所有权重配置");
        
        List<CourseWeightConfig> configs = courseWeightConfigRepository.findAll();
        
        return configs.stream().map(config -> {
            String courseName = courseRepository.findById(config.getCourseId())
                .map(course -> course.getName())
                .orElse("未知课程");
            
            return WeightConfigResponse.builder()
                .id(config.getId())
                .courseId(config.getCourseId())
                .courseName(courseName)
                .attendanceWeight(config.getAttendanceWeight())
                .homeworkWeight(config.getHomeworkWeight())
                .labWeight(config.getLabWeight())
                .quizWeight(config.getQuizWeight())
                .midtermWeight(config.getMidtermWeight())
                .isActive(config.getIsActive())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .isDefault(false)
                .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteWeightConfig(Long id) {
        log.info("删除权重配置: {}", id);
        
        boolean exists = courseWeightConfigRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("权重配置不存在: " + id);
        }
        
        courseWeightConfigRepository.deleteById(id);
    }
    
    private WeightConfigResponse createDefaultConfig(Long courseId) {
        String courseName = courseRepository.findById(courseId)
            .map(course -> course.getName())
            .orElse("未知课程");
        
        return WeightConfigResponse.builder()
            .courseId(courseId)
            .courseName(courseName)
            .attendanceWeight(BigDecimal.valueOf(20.0))
            .homeworkWeight(BigDecimal.valueOf(20.0))
            .labWeight(BigDecimal.valueOf(20.0))
            .quizWeight(BigDecimal.valueOf(20.0))
            .midtermWeight(BigDecimal.valueOf(20.0))
            .isActive(true)
            .description("默认权重配置")
            .isDefault(true)
            .build();
    }
}