package com.example.studentanalysissystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "employee_number", unique = true, nullable = false, length = 50)
    private String employeeNumber;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
    
    @Column(name = "has_custom_avatar", nullable = false)
    private Boolean hasCustomAvatar = false;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
