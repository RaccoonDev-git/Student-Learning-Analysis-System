package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_weight_configs")
@Data
public class CourseWeightConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_id", nullable = false, unique = true)
    private Long courseId;
    
    @Column(name = "attendance_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal attendanceWeight = BigDecimal.valueOf(20.00);
    
    @Column(name = "homework_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal homeworkWeight = BigDecimal.valueOf(20.00);
    
    @Column(name = "lab_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal labWeight = BigDecimal.valueOf(20.00);
    
    @Column(name = "quiz_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal quizWeight = BigDecimal.valueOf(20.00);
    
    @Column(name = "midterm_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal midtermWeight = BigDecimal.valueOf(20.00);
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "description")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 验证权重总和是否为100%
    @PrePersist
    @PreUpdate
    public void validateWeights() {
        BigDecimal total = attendanceWeight.add(homeworkWeight).add(labWeight)
                .add(quizWeight).add(midtermWeight);
        if (total.compareTo(BigDecimal.valueOf(100.00)) != 0) {
            throw new IllegalArgumentException("权重总和必须为100%");
        }
    }
}