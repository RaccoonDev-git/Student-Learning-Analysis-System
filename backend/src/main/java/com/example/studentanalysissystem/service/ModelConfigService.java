package com.example.studentanalysissystem.service;

import com.example.studentanalysissystem.service.ai.ModelConfig;

import java.util.List;

/**
 * 模型配置服务接口
 */
public interface ModelConfigService {
    
    /**
     * 获取所有模型配置
     */
    List<ModelConfig> getAllConfigs();
    
    /**
     * 根据模型名称获取配置
     */
    ModelConfig getConfig(String modelName);
    
    /**
     * 保存模型配置
     */
    ModelConfig saveConfig(ModelConfig config);
    
    /**
     * 更新模型配置
     */
    ModelConfig updateConfig(String modelName, ModelConfig config);
    
    /**
     * 删除模型配置
     */
    boolean deleteConfig(String modelName);
    
    /**
     * 启用/禁用模型
     */
    boolean toggleModel(String modelName, boolean enabled);
    
    /**
     * 测试模型连接
     */
    boolean testConnection(String modelName);
}
