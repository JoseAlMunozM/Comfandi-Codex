package com.comfandi.korlon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    NO_COBRADO("NO COBRADO"),
    COBRADO("COBRADO"),
    POR_REVISAR("POR REVISAR");

    private final  String value;

    public static UserStatus getStatus(final String value) {
        for (UserStatus p : UserStatus.values()) {
            if (p.value.equals(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
