package com.comfandi.phobos.service.enums;

import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Providers {
    EMPRENDIMIENTO_ACTIVOS("EMPRENDIMIENTO ACTIVOS"),
    EMPRENDIMIENTO_CESANTES("EMPRENDIMIENTO CESANTES"),
    EDUCACION_ACTIVOS("EDUCACION ACTIVOS"),
    EDUCACION_CESANTES("EDUCACION CESANTES"),
    FOMENTO_TH_FOSFEC("FOMENTO TH FOSFEC"),
    DEFAULT("");
    public final String value;

    public static Providers getProvider(final String value) {
        for (Providers p : Providers.values()) {
            if (p.value.equals(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public static Providers getProviderByUserType(UserType userType){
        if(userType==UserType.ACTIVO){
            return EMPRENDIMIENTO_ACTIVOS;
        }else if(userType==UserType.CESANTE){
            return EMPRENDIMIENTO_CESANTES;
        }
        return DEFAULT;
    }
}
