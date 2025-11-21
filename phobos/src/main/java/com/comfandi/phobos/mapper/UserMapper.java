package com.comfandi.phobos.mapper;

import com.comfandi.phobos.client.response.ApiCorrectUser;
import com.comfandi.phobos.client.response.ApiUserNotAceptableResponse;
import com.comfandi.phobos.client.response.UserNotAcceptableResponse;
import com.comfandi.phobos.entity.UserEntity;
import com.comfandi.phobos.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UserMapper {

    public static DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public UserEntity userDtoToUserEntity(UserDto dto) {

        return UserEntity.builder()
                .identificationNumber(dto.getIdentification().trim())
                .identificationType(dto.getIdentificationType())
                .name(dto.getFullName())
                .trainingDate(LocalDate.parse(dto.getStartDate()))
                .trainingEndDate(dto.getEndDate()!=null?LocalDate.parse(dto.getEndDate()):null)
                .cellphone(dto.getMobile())
                .email(dto.getEmail())
                .courseFee(dto.getCourseFee())
                .status(dto.getState())
                .description(dto.getDescription())
                .advanceCourse(BigDecimal.valueOf(Double.parseDouble(dto.getProgress())))
                .build();
    }


    public ApiUserNotAceptableResponse userListToNotAcceptableResponse(Page<UserEntity> userList){
        ApiUserNotAceptableResponse response = new ApiUserNotAceptableResponse();
        response.setUsers(userList.stream().map(this::userToApiUserNotAceptableResponse).toList());
        response.setSize(userList.getSize());
        response.setPage(userList.getNumber());
        response.setTotalPages(userList.getTotalPages());
        response.setTotalElements(userList.getTotalElements());
        return  response;
    }

    public UserNotAcceptableResponse userToApiUserNotAceptableResponse(UserEntity user){

        UserNotAcceptableResponse response= new UserNotAcceptableResponse();
        response.setAccount(user.getAccountId().toString());
        response.setIdentification(user.getIdentificationNumber());
        response.setFullName(user.getName());
        response.setReason(user.getDescription());
        response.setStatus(user.getStatus());
        response.setDate(dateTimeFormatter.format(user.getChargeDate()));
        return response;
    }

    public ApiCorrectUser userToApiCorrectUser(UserEntity user, String oldStatus, String oldDescription){
        ApiCorrectUser apiCorrectUser= new ApiCorrectUser();
        apiCorrectUser.setIdentificationNumber(user.getIdentificationNumber());
        apiCorrectUser.setNewStatus(user.getStatus());
        apiCorrectUser.setOldStatus(oldStatus);
        apiCorrectUser.setOldReason(oldDescription);
        return  apiCorrectUser;
    }

    public UserDto userEntityToUserDto(UserEntity user){
        return UserDto.builder()
                .userId(user.getId())
                .identification(user.getIdentificationNumber())
                .identificationType(user.getIdentificationType())
                .fullName(user.getName())
                .mobile(user.getCellphone())
                .charge(user.getStatus())
                .regional(user.getRegional().getName())
                .description(user.getDescription())
                .courseFee(user.getCourseFee())
                .email(user.getEmail())
                .program(user.getCourse().getName())
                .provider(user.getCourse().getProvider().getName())
                .startDate(user.getTrainingDate().format(dateTimeFormatter))
                .endDate(user.getTrainingEndDate()==null?"":user.getTrainingEndDate().format(dateTimeFormatter))
                .mode(user.getCourse().getModality().getName())
                .progress(user.getAdvanceCourse().toString())
                .programId(user.getCourse().getId().toString())
                .portfolioId(user.getCourse().getPortfolio()!=null?user.getCourse().getPortfolio().getId().toString():null)
                .build();
    }
}
