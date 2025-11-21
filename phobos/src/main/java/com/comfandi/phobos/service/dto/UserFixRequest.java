package com.comfandi.phobos.service.dto;

import lombok.Data;

@Data
public class UserFixRequest {
    private String documentType;
    private String documentNumber;
    private String newEstadoFinal;
    private String observacion;
}