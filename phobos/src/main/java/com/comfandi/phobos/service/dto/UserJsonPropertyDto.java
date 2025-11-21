package com.comfandi.phobos.service.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserJsonPropertyDto {

    @JsonProperty("Regional")
    private String regional;

    @JsonProperty("Programa")
    private String program;

    @JsonProperty("Programa_estandarizado")
    private String standardProgram;

    @JsonProperty("Modalidad")
    private String mode;

    @JsonProperty("Matricula")
    private String registration;

    @JsonProperty("Linea_atencion")
    private String attentionLine;

    @JsonProperty("Fec_desde")
    private String startDate;

    @JsonProperty("Fec_hasta")
    private String endDate;

    @JsonProperty("Identificacion")
    private String identification;

    @JsonProperty("Tipo_identificacion")
    private String identificationType;

    @JsonProperty("Nombre_completo")
    private String fullName;

    @JsonProperty("Celular")
    private String mobile;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Avance")
    private String progress;

    @JsonProperty("Retiro")
    private String withdrawal;

    @JsonProperty("Aprobacion")
    private String approval;

    @JsonProperty("Cobro")
    private String charge;

    @JsonProperty("Comprobante")
    private String receipt;

    @JsonProperty("userId")
    private Long userId;

    private String state;

    private String description;

    private BigDecimal courseFee;

    private String fechaRemision;

    private String regionalResidencia;

    private String provider;

    private String businessName;

    private String businessId;

    private String portfolioId;

    private String programId;

    private boolean existingProgram;
}