package com.comfandi.korlon.manager.data;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class UnavailableUsers {

    @JsonProperty("Identificacion")
    private String identificacion;

    @JsonProperty("Tipo_identificacion")
    private String tipoIdentificacion;

    @JsonProperty("Nombre_completo")
    private String nombreCompleto;

    @JsonProperty("Celular")
    private String celular;

    @JsonProperty("Fec_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecInicio;

    @JsonProperty("Fec_inhabilitacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecInhabilitacion;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Avance")
    private String avance;

    @JsonProperty("Causa")
    private String causa;

    @JsonProperty("Notificacion")
    private Boolean notificacion;

    @JsonProperty("userId")
    private Long userId;

}