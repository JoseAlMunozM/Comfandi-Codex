package com.comfandi.phobos.controller;

import com.comfandi.phobos.service.WorkshopUserService;
import com.comfandi.phobos.service.dto.WorkshopUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/workshops")
public class ApiWorkshopUsersController {

    @Autowired
    WorkshopUserService workshopUserService;

    @GetMapping("/users")
    public ResponseEntity<List<WorkshopUserDto>>  processWorkshop(){
        System.out.println("Workshops");
        List<WorkshopUserDto> users=workshopUserService.getWorkshopsUsers();
        return ResponseEntity.ok(users);
    }
}
