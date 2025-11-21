package com.comfandi.phobos.service;

import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiUserRequest;
import com.comfandi.phobos.client.response.ApiUnavailableUserApprovalResponse;
import com.comfandi.phobos.client.response.ApiUnavailableUserStatusResponse;
import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.entity.UnavailableUserEntity;
import com.comfandi.phobos.mapper.UnavailableUserMapper;
import com.comfandi.phobos.repository.UnavailableUserRepository;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UnavailableUsersProcessingService {

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private UnavailableUserRepository unavailableUserRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ApiUnavailableUsersResponse> getUnableUsersProcess() {
        Mono<List<ApiUnavailableUsersResponse>> users = externalServiceClient.fetchUnavailableFromService("token");
        List<ApiUnavailableUsersResponse> res = users.block();
        //validate
        if (res == null) return null;
        List<ApiUnavailableUsersResponse> resValidated =validateUnavailableUserApproval(res);
        List<ApiUnavailableUsersResponse> resValidateStatus=validateUnavailableStatusUser(resValidated);
        List<ApiUnavailableUsersResponse> resValidateAsistance=validateAsistanceUsers(resValidateStatus);

        return  save(resValidateAsistance);
    }

    private List<ApiUnavailableUsersResponse> validateUnavailableUserApproval(List<ApiUnavailableUsersResponse> users) {
        List<ApiUserRequest> userRequests = users.stream()
                .map(user -> new ApiUserRequest(user.getIdentification(), "CC")).toList();
        List<ApiUnavailableUserApprovalResponse> approvalList = externalServiceClient.fetchUnavailableApprovalFromService(userRequests, "token").block();
        if (approvalList == null) {
            approvalList = new ArrayList<>();
        }
        Map<String, ApiUnavailableUserApprovalResponse> respuesta = approvalList
                .stream().collect(Collectors.toMap(ApiUnavailableUserApprovalResponse::getDocument, Function.identity()));
        for (ApiUnavailableUsersResponse user : users) {
            ApiUnavailableUserApprovalResponse approval = respuesta.get(user.getIdentification());
            if (approval != null) {
                validateStartDate(user,approval);
            } else {
                user.setState(Message.AVAILABLE);
                user.setDescription("Sin fecha de aprobacion");
            }
        }
        return users;
    }

    private List<ApiUnavailableUsersResponse> validateUnavailableStatusUser(List<ApiUnavailableUsersResponse> users) {
        List<ApiUserRequest> userRequests = users.stream()
                .map(user -> new ApiUserRequest(user.getIdentification(), "CC")).toList();
        List<ApiUnavailableUserStatusResponse> statusList = externalServiceClient.fetchUnavailableStatusFromService(userRequests, "token").block();
        if (statusList == null) {
            statusList = new ArrayList<>();
        }
        Map<String, ApiUnavailableUserStatusResponse> respuesta = statusList
                .stream().collect(Collectors.toMap(ApiUnavailableUserStatusResponse::getDocument, Function.identity()));
        for (ApiUnavailableUsersResponse user : users) {
            ApiUnavailableUserStatusResponse status = respuesta.get(user.getIdentification());
            if (status != null) {
                if(!user.getState().equals(Message.UNAVAILABLE)){
                    user.setState(Message.AVAILABLE);
                    user.setDescription("OK");

                }
            } else {
                user.setState(Message.UNAVAILABLE);
                user.setDescription("En convocatoria");
            }
        }
        return users;
    }

    private void validateStartDate(ApiUnavailableUsersResponse user, ApiUnavailableUserApprovalResponse response) {
        boolean validate=false;
        if (response != null) {
            if (response.getApprovalDate() != null) {
                LocalDate startDate = LocalDate.parse(user.getStartDate(), formatter);
                LocalDate approvalDate = LocalDate.parse(response.getApprovalDate(), formatter);
                long days = ChronoUnit.DAYS.between(startDate,approvalDate );
                if (days < 30) {
                    user.setState(Message.AVAILABLE);
                    user.setDescription("OK");
                } else {
                    user.setState(Message.UNAVAILABLE);
                    user.setDescription("Fecha de aprobacion superior a 30 dias al inicio de formacion");
                }
                validate=true;
            }
        }
        if(!validate){
            user.setState(Message.UNAVAILABLE);
            user.setDescription("Sin fecha de aprobacion");
        }
    }

    private List<ApiUnavailableUsersResponse> validateAsistanceUsers(List<ApiUnavailableUsersResponse> users){
        for (ApiUnavailableUsersResponse user : users) {
            validateAssistance(user);
        }
        return users;
    }

    private void validateAssistance(ApiUnavailableUsersResponse user) {
        boolean validate=false;
        if (user.getProgress() != null) {
            if (Double.parseDouble(user.getProgress()) >= 80) {
                user.setState(Message.AVAILABLE);
                user.setDescription("OK");
                validate=true;
            }
        }
        if(!validate){
            user.setState(Message.UNAVAILABLE);
            user.setDescription(Message.NOT_AVAILABLE_ASSISTANT_BELLOW_80_PERCENT);
        }
    }

    private List<ApiUnavailableUsersResponse> save(List<ApiUnavailableUsersResponse> users){
        List<UnavailableUserEntity> userList= unavailableUserRepository.saveAll(users.stream().map(UnavailableUserMapper::unavailableUserToUnavailableUserEntity).toList());

        return  userList.stream().map(UnavailableUserMapper::unavailableUserEntityToUnavailableUser).toList();
    }

}
