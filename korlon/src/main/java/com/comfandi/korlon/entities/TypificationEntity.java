package com.comfandi.korlon.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "typification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "location")
    private String location;

    @Column(name = "cebe")
    private String cebe;

    @Column(name = "account")
    private String account;

    @Column(name = "assignment")
    private String assignment;

    @Column(name = "type")
    private String type;
}
