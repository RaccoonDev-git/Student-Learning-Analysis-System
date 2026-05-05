package com.example.studentanalysissystem.dto.response;

import com.example.studentanalysissystem.model.LearningActivity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学习活动响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningActivityResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private ActivityType activityType;
    private String activityTypeDesc;
    private Map<String, Object> activityData;
    private Integer duration; // 持续时间(分钟)
    private LocalDateTime createdAt;

    /**
     * 格式化的活动描述
     */
    public String getFormattedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(activityTypeDesc);

        if (courseName != null) {
            sb.append("：").append(courseName);
        }

        if (activityData != null && !activityData.isEmpty()) {
            if (activityData.containsKey("title")) {
                sb.append(" - ").append(activityData.get("title"));
            }
            if (activityData.containsKey("description")) {
                sb.append(" (").append(activityData.get("description")).append(")");
            }
        }

        return sb.toString();
    }
}
