package com.comfandi.korlon.manager.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoValidacionPlantillaRow {

    private String item;
    private String clave;
    private String account;
    private String cebe;
    private String asignation;
    private String text;
    private Double sumValue;
}
