package com.comfandi.phobos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cursos_crea_validados", schema = "fomento")
public class CourseValidated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "regional")
    private String regional;

    @Column(name = "programa")
    private String programa;

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "programa_estandarizado")
    private String programaEstandarizado;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "linea_atencion")
    private String lineaAtencion;

    @Column(name = "fec_desde")
    private LocalDate fecDesde;

    @Column(name = "fec_hasta")
    private LocalDate fecHasta;

    @Column(name = "identificacion")
    private String identificacion;

    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "celular")
    private String celular;

    @Column(name = "email")
    private String email;

    @Column(name = "avance")
    private Double avance;

    @Column(name = "retiro")
    private Boolean retiro;

    @Column(name = "aprobacion")
    private Boolean aprobacion;

    @Column(name = "cobro")
    private Boolean cobro;

    @Column(name = "comprobante")
    private String comprobante;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;
}