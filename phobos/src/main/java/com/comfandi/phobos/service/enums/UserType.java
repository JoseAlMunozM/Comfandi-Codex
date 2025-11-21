package com.comfandi.phobos.service.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserType {

    ACTIVO(List.of("Trabajadores activos", "Educación_Trabajadores Activos")),
    CESANTE(List.of("Fosfec" , "Educación_ Fosfec")),
    TALLERES(List.of("TH-Fosfec")),
    DEFAULT(Collections.emptyList());

    private final List<String> value;


    public static UserType fromValue(String value){
        for(UserType userType : UserType.values()){
            if(userType.value.contains(value)){
                return userType;
            }
        }
        return DEFAULT;
    }

    public static String validateProfile(UserType type){
        return switch (type){
            case ACTIVO -> "activos-empresarial";
            case CESANTE -> "cesantes-empresarial";
            case TALLERES -> "fomento-th-fosfec";
            case DEFAULT -> "Sin perfil";
        };
    }





}
