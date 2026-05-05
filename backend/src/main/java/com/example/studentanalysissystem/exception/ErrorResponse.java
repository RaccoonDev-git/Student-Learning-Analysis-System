package com.example.studentanalysissystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一错误响应格式
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * HTTP状态码
     */
    private Integer status;

    /**
     * 错误类型
     */
    private String error;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 详细错误信息(如验证错误的字段详情)
     */
    private Map<String, String> details;
}
