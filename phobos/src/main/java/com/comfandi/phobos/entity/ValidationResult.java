package com.comfandi.phobos.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "resultados_validacion", schema = "fomento")
public class ValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private CourseValidated usuario;

    @Column(name = "program")
    private String programa;

    @Column(name = "course_standard") 
    private String cursoEstandarizado;

    @Column(name = "fechavalidacion")
    private LocalDate fechavalidacion;

    @Column(name = "estadofinal")
    private String estadofinal;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "cutoffdate")
    private LocalDate cutoffdate;

    @Column(name = "documentid")
    private Long documentid;

    
}