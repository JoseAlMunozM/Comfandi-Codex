package com.comfandi.korlon.enums;

import lombok.Getter;
import java.util.Optional;

@Getter
public enum Profiles {
    ACTIVOS_EMPRESARIAL("activos-empresarial"),
    ACTIVOS_EDUCACION("activos-educacion"),
    CESANTES_EMPRESARIAL("cesantes-empresarial"),
    CESANTES_EDUCACION("cesantes-educacion"),
    TH_FOSFEC("fomento-th-fosfec");

    private final String value;

    Profiles(String value) {
        this.value = value;
    }

    /**
     * Acepta tanto names del enum como su value.
     */
    public static Optional<Profiles> fromString(String value) {
        if (value == null) return Optional.empty();
        String normalized = value.trim().replace("_", "-");

        for (Profiles profile : Profiles.values()) {

            // Comparar contra el nombre del enum
            if (profile.name().equalsIgnoreCase(value)) {
                return Optional.of(profile);
            }

            // Comparar contra el value del enum
            if (profile.value.equalsIgnoreCase(normalized)) {
                return Optional.of(profile);
            }
        }
        return Optional.empty();
    }

    public static String getLawByProfile(Profiles profile) {
        return switch (profile) {
            case ACTIVOS_EMPRESARIAL, ACTIVOS_EDUCACION -> "Ley 2069";
            case CESANTES_EDUCACION, CESANTES_EMPRESARIAL, TH_FOSFEC -> "Ley 1636";
        };
    }
}
