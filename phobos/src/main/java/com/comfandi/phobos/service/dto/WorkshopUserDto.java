package com.comfandi.phobos.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkshopUserDto {

    @JsonProperty("Regional")
    private String regional;

    @JsonProperty("Modalidad")
    private String modalidad;

    @JsonProperty("Ciudad")
    private String city;

    @JsonProperty("Capacitacion")
    private String program;

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

    @JsonProperty("Fec_inicio")
    private String startDate;


    @JsonProperty("Avance")
    private String progress;

    @JsonProperty("Aprobacion")
    private String approval;

    @JsonProperty("Cobro")
    private String charge;

    @JsonProperty("Periodo")
    private Integer year;

    @JsonProperty("UserId")
    private Long userId;

    private String orientationDate;

    private String state;

    private String description;

    private BigDecimal courseFee;

    private String provider;

}