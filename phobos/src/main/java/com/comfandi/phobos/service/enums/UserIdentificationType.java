package com.comfandi.phobos.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserIdentificationType {
    NIT("Número de Identificacion Tributaria","CC"),
    SIE("Sin identificacion del exterior","SIE"),
    NUIP("Número Unico de Identificacion Personal","NUIP"),
    SC("Salvoconducto de permanencia","SC"),
    CC("cedula","CC"),
    TI("Tarjeta de identidad","TI"),
    PA("Pasaporte","PA"),
    CE("cedula de extranjeria","CE"),
    RC("Registro civil","RC"),
    PEP("Permiso especial de permanencia", "PEP"),
    CD("Carnet diplomático","CD"),
    TE("Tarjeta de extranjeria","TE"),
    PPT("Permiso por Proteccion Temporal","PPT");

    private final String name;
    private final String acronym;


    public static UserIdentificationType findIdentificationTypeByName(String name){
        for(UserIdentificationType type:UserIdentificationType.values()){
            if(type.name.equalsIgnoreCase(name)){
                return type;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
