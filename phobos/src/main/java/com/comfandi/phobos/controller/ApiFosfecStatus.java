package com.comfandi.phobos.controller;


import com.comfandi.phobos.service.FosfecStatusService;
import com.comfandi.phobos.service.dto.AppointmentQueryRequest;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserFosfecResultDto;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("/api/v1/fosfec-status")
public class ApiFosfecStatus {

    @Autowired
    private FosfecStatusService fosfecStatusService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsersWithFosfecStatus() {
        try {
            List<UserFosfecResultDto> users = fosfecStatusService.getUsersWithFosfecStatus();

            if (users == null || users.isEmpty()) {
                return ResponseEntity.ok("No users found with fosfec status.");
            }

            return ResponseEntity.ok(users);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Internal server error: " + e.getMessage());
        }
    }



    @PostMapping("/appointments/query")
    public ResponseEntity<?> queryAppointments() {
        try {

            List<UserFosfecResultDto> users = fosfecStatusService.getUsersWithFosfecStatus();

            if (users == null || users.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No users available for appointment validation.");
            }

            Object response = fosfecStatusService.callAppointmentsApi(users);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error calling appointment service: " + e.getMessage());
        }
    }
}