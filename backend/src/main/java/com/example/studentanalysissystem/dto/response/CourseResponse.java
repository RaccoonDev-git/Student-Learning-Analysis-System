package com.example.studentanalysissystem.dto.response;

import com.example.studentanalysissystem.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Long teacherId;
    private String teacherName;
    private Integer credits;
    private String semester;
    private String academicYear;
    private Integer maxStudents;
    private Integer enrolledCount;
    private Course.CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
