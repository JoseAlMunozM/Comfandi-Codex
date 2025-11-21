package com.comfandi.phobos.service.enums;

import lombok.Getter;

@Getter
public enum SourceType {

    COURSES("Cursos"),
    WORKSHOP("Talleres");

    private final String value;

    SourceType(String value) {
        this.value = value;
    }
}
