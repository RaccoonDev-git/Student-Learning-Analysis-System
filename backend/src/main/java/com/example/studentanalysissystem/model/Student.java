package com.example.studentanalysissystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "student_number", unique = true, nullable = false, length = 50)
    private String studentNumber;

    @Column(name = "class", length = 100)
    private String className;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "enrollment_date")
    private java.time.LocalDate enrollmentDate;

    @Column(name = "graduation_date")
    private java.time.LocalDate graduationDate;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
    
    @Column(name = "has_custom_avatar", nullable = false)
    private Boolean hasCustomAvatar = false;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEnrollment> courseEnrollments;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
