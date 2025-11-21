package com.comfandi.korlon.mapper;

import com.comfandi.korlon.api.response.ApiUserNotAcceptableResponse;
import com.comfandi.korlon.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {

    public ApiUserNotAcceptableResponse userEntityToUserNotAcceptable(UserEntity user) {
        ApiUserNotAcceptableResponse response = new ApiUserNotAcceptableResponse();
        response.setAccount(user.getAccountId().toString());
        response.setStatus(user.getStatus());
        response.setReason(user.getDescription());
        return response;
    }
}
