package com.example.studentanalysissystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "exam_type", length = 20)
    private String examType;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "grade_level", length = 5)
    private String gradeLevel;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculateGrade() {
        if (score != null && totalScore != null && totalScore.compareTo(BigDecimal.ZERO) > 0) {
            percentage = score.divide(totalScore, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
                gradeLevel = "A";
            } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
                gradeLevel = "B";
            } else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
                gradeLevel = "C";
            } else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
                gradeLevel = "D";
            } else {
                gradeLevel = "F";
            }
        }
    }
}
