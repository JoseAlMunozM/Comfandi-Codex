package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_course",nullable = false)
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "id_regional", nullable = false)
    private RegionalEntity regional;


    @Column(name = "identification_number", length = 50, nullable = false, unique = true)
    private String identificationNumber;

    @Column(name = "identification_type", nullable = false)
    private String identificationType;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "cellphone", length = 20)
    private String cellphone;

    @Column(name = "advance_course", precision = 5, scale = 2)
    private BigDecimal advanceCourse;

    @Column(name = "training_date")
    private LocalDate trainingDate;

    @Column(name = "training_end_date")
    private LocalDate trainingEndDate;

    @Column(name = "course_fee")
    private BigDecimal courseFee;

    @Column(name = "charge_date")
    private LocalDate chargeDate;

    @Column(name = "creation_date", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creationDate;

    @Column(name = "update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updateDate;

    @Column(name = "account_id")
    private Integer accountId;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}