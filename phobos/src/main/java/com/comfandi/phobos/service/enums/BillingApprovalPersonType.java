package com.comfandi.phobos.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BillingApprovalPersonType {
    JEFE("JEFE"),
    ANALISTA("ANALISTA");

    private final String value;

    public static BillingApprovalPersonType fromValue(String v) {
        for (BillingApprovalPersonType c : BillingApprovalPersonType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
