package com.comfandi.korlon.manager.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatosPlantilla {

    private String regional;
    private String program;
    private String programResume;
    private String mode;
    private Double sum;
    private String resume;
    private Long cant;
    private String account;
    private String cebe;
    private String textRegistry;
    private String assignation;
    private int textLength;
    private Double downPayment1;
    private Double downPayment2;
    private Double downPayment3;
}
