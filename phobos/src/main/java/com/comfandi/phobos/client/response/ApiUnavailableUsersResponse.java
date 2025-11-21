package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ApiUnavailableUsersResponse {

    @JsonProperty("Identificacion")
    private String identification;

    @JsonProperty("Tipo_identificacion")
    private String identificationType;

    @JsonProperty("Nombre_completo")
    private String fullName;

    @JsonProperty("Celular")
    private String mobile;

    @JsonProperty("Fec_inicio")
    private String startDate;

    @JsonProperty("Fec_inhabilitacion")
    private String unavailableDate;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Avance")
    private String progress;

    @JsonProperty("Causa")
    private String cause;

    @JsonProperty("Notificacion")
    private boolean notification;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String state;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    private Long userId;

}
