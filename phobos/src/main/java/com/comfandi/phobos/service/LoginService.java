package com.comfandi.phobos.service;

import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiLoginRequest;
import com.comfandi.phobos.client.response.ApiLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final ExternalServiceClient externalServiceClient;

    public String login(ApiLoginRequest apiLoginRequest) {
        ApiLoginResponse apiLoginResponse = externalServiceClient.loginFromService(apiLoginRequest.getUsername(), apiLoginRequest.getPassword());
        return apiLoginResponse.getToken();
    }
}
