package com.comfandi.phobos.mapper;

import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.entity.UnavailableUserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UnavailableUserMapper {

    public static DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static UnavailableUserEntity unavailableUserToUnavailableUserEntity(ApiUnavailableUsersResponse users){
        return UnavailableUserEntity.builder()
                .unavailableDate(LocalDate.parse(users.getUnavailableDate(),dateTimeFormatter))
                .fullName(users.getFullName())
                .identificationNumber(users.getIdentification())
                .identificationType(users.getIdentificationType())
                .reason(users.getCause())
                .build();
    }

    public static ApiUnavailableUsersResponse unavailableUserEntityToUnavailableUser(UnavailableUserEntity entity){
        return ApiUnavailableUsersResponse.builder()
                .userId(entity.getId())
                .cause(entity.getReason())
                .unavailableDate(dateTimeFormatter.format(entity.getUnavailableDate()))
                .fullName(entity.getFullName())
                .identification(entity.getIdentificationNumber())
                .identificationType(entity.getIdentificationType()).build();
    }
}
