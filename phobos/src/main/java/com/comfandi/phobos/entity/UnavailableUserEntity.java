package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@Data
@Table(name = "unavailable_users")
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "unavailable_users_seq", sequenceName = "unavailable_users_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "identification_number")
    private String identificationNumber;
    @Column(name = "identification_type")
    private String identificationType;
    @Column(name = "name")
    private String fullName;
    @Column(name = "unavailable_date")
    private LocalDate unavailableDate;
    @Column(name = "description")
    private String reason;

}
