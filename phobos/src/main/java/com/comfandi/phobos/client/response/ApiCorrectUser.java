package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiCorrectUser {

    @JsonProperty("Documento")
    private String identificationNumber;
    @JsonProperty("Estado_anterior")
    private String oldStatus;
    @JsonProperty("Nuevo_estado")
    private String newStatus;
    @JsonProperty("Motivo_anterior")
    private String oldReason;
}
