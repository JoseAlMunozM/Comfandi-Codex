package com.comfandi.phobos.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBillingApprovalDto {

    @JsonProperty("idCuentaCobro")
    private Integer billingAccountId;

    @JsonProperty("documento")
    private String identificationNumber;

    @JsonProperty("tipo_de_usuario")
    private String userType;

    @JsonProperty("aprobación")
    private String approval;

    @JsonProperty("observación")
    private String observation;

}