package com.comfandi.korlon.manager.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AccountGroup {

    private int documentGroup;
    private Map<String, String> account;
    private Map<String, String> beneficiary;
}
