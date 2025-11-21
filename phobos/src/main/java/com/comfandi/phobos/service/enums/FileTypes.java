package com.comfandi.phobos.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypes {
    PDF("pdf"),
    EXCEL("excel");

    public final String value;

    public static FileTypes getFileType(final String value) {
        for (FileTypes ft : FileTypes.values()) {
            if (ft.value.equals(value)) {
                return ft;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
