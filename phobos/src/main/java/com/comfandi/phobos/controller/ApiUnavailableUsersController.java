package com.comfandi.phobos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.phobos.service.UnavailableUsersService;


@RestController
@RequestMapping("/api/v1/unavailable-users")
public class ApiUnavailableUsersController {

    @Autowired
    UnavailableUsersService unavailableUsersService;

    @GetMapping("/find-users")
    public ResponseEntity<?> findUnavailableUsers(@RequestParam String startDate, @RequestParam String endDate){
        return ResponseEntity.ok(unavailableUsersService.listUnavailableUsers(startDate,endDate));
    }
}
