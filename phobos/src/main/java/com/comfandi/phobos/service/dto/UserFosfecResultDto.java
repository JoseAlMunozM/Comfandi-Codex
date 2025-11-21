package com.comfandi.phobos.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFosfecResultDto {

    private String identificationType;
    private String identification;
    private String fullName;

    private String estadoFinal;
    private String observacion;
}