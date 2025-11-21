package com.comfandi.korlon.enums;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public enum SourceType {
    COURSES("Cursos"),
    WORKSHOPS("Talleres");

    private final String value;

    public static Optional<SourceType> fromString(String value) {
        for (SourceType source : SourceType.values()) {
            if (source.value.equals(value)) {
                return Optional.of(source);
            }
        }
        return Optional.empty();
    }
}
