package com.comfandi.phobos.service.enums;

import lombok.AllArgsConstructor;

import lombok.Getter;


@Getter
@AllArgsConstructor
public enum BillingApprovalStatus {
    APPROVED("APROBADO"),
    REJECTED("RECHAZADO");

    public final String value;

    public static BillingApprovalStatus fromValue(String v) {
        for (BillingApprovalStatus c : BillingApprovalStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
