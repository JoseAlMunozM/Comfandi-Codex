package com.comfandi.korlon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Providers {
    EMPRESARIAL("Desarollo empresarial"),
    EDUCACION("Educacion");

    private final String value;


    public static Providers getProviders(final String value) {
        for (Providers p : Providers.values()) {
            if (p.value.equals(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
