package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.dto.request.WeightConfigRequest;
import com.example.studentanalysissystem.dto.response.WeightConfigResponse;

import java.util.List;

/**
 * 权重配置服务接口（简化版）
 */
public interface WeightConfigService {

    /**
     * 获取课程权重配置
     */
    WeightConfigResponse getWeightConfigByCourseId(Long courseId);

    /**
     * 创建或更新权重配置
     */
    WeightConfigResponse createOrUpdateWeightConfig(WeightConfigRequest request);

    /**
     * 获取所有权重配置
     */
    List<WeightConfigResponse> getAllWeightConfigs();

    /**
     * 删除权重配置
     */
    void deleteWeightConfig(Long id);
}
