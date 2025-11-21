package com.comfandi.phobos.controller;

import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.service.UnavailableUsersProcessingService;
import com.comfandi.phobos.mapper.InformationDatabaseMapper;
import com.comfandi.phobos.util.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unavailable-users")
public class ApiUnableUsersController {

    @Autowired
    private UnavailableUsersProcessingService unavailableUsersProcessingService;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private InformationDatabaseMapper informationDatabaseMapper;

    @Autowired
    private ApiConfig apiConfig;


    @GetMapping("/users")
    public ResponseEntity<?> unavailableUsers(){
        List<ApiUnavailableUsersResponse> unavailableUsers = unavailableUsersProcessingService.getUnableUsersProcess();
        String response=externalServiceClient.fetchUnavailableNotificationFromService(unavailableUsers.stream()
                .map(x -> informationDatabaseMapper.unavailableUsersResponseToApiInformationDatabase(x))
                .toList(),apiConfig.getEmailNotification());
        if(response.equals("OK"))
            return ResponseEntity.status(HttpStatus.OK).body(unavailableUsers);
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error en la generacion y envio");
    }
    

}