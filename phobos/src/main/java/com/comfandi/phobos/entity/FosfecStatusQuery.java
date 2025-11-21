package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "fosfec_status_query")
@Data
public class FosfecStatusQuery {

    @Id
    @Column(name = "secuencia")
    private Long id;

    @Column(name = "tipo_doc")
    private String documentType;

    @Column(name = "cedula")
    private String documentNumber;

    @Column(name = "primer_apellido")
    private String firstLastName;

    @Column(name = "segundo_apellido")
    private String secondLastName;

    @Column(name = "primer_nombre")
    private String firstName;

    @Column(name = "segundo_nombre")
    private String secondName;

    @Column(name = "fijo")
    private String phoneNumber;

    @Column(name = "celular")
    private String mobileNumber;

    @Column(name = "correo")
    private String email;

    @Column(name = "origen")
    private String programName;

    @Column(name = "estado_solicitud")
    private String status;

    @Column(name = "fecha_creacion")
    private LocalDateTime approvalDate;

}