package com.comfandi.korlon.manager.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DatosPlantillaHeader {
    private String concept;
    private String conceptObject;
    private String program;
    private String date;
    private String etapa;
}
