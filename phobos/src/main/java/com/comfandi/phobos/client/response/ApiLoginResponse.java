package com.comfandi.phobos.client.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiLoginResponse {
    private String message;
    private String token;
}
