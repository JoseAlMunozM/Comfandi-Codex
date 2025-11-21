package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workshop_users")
@Getter
@Setter
public class WorkshopUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "workshop_users_seq", sequenceName = "workshop_user_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_workshop",nullable = false)
    private WorkshopEntity workshop;

    @ManyToOne
    @JoinColumn(name = "id_modality",nullable = false)
    private ModalityEntity modality;

    @ManyToOne
    @JoinColumn(name = "id_regional",nullable = false)
    private RegionalEntity regional;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "identification_number")
    private String identificationNumber;

    @Column(name = "identification_type")
    private String identificationType;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "fee_value")
    private Double value;

    @Column(name = "orientation_date")
    private LocalDate orientationDate;

    @Column(name = "appointment_date")
    private LocalDate startDate;

    @Column(name = "validation_Date")
    private LocalDate validationDate;

    @Column(name = "appointment")
    private Boolean appointment;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "year")
    private Integer year;

    @Column(name = "progress")
    private Double progress;

    @Column(name = "id_account")
    private Integer accountId;

    @Column(name = "phone")
    private  String phone;

}
