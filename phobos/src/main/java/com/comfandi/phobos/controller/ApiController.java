package com.comfandi.phobos.controller;

import com.comfandi.phobos.client.response.ApiCorrectUser;
import com.comfandi.phobos.client.response.ApiUserNotAceptableResponse;
import com.comfandi.phobos.client.response.UserNotAcceptableResponse;
import com.comfandi.phobos.entity.BillingAccountEntity;
import com.comfandi.phobos.exception.GenericException;
import com.comfandi.phobos.service.BillingAccountServices;
import com.comfandi.phobos.service.UserBlockService;
import com.comfandi.phobos.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.comfandi.phobos.service.UserProcessingService;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserJsonPropertyDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/courses")
public class ApiController {

    @Autowired
    private UserProcessingService userProcessingService;
    
    @Autowired
    private UserBlockService userBlockService;
    
    @Autowired
    private BillingAccountServices billingAccountServices;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> index() {
        List<UserDto> users = userProcessingService.processUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{accountId}")
    public  ResponseEntity<?> generateDocument(@PathVariable Long accountId){
        BillingAccountEntity ba= billingAccountServices.getBillingAccount(accountId);
        if(ba!=null){
            List<UserDto> users = userProcessingService.generateAccountDocuments(accountId,ba.getAmortizable());
            if(users.isEmpty()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("cuenta de cobro no existe");
            }
            return ResponseEntity.ok(users);
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("cuenta de cobro no existe");
        }
    }

    @GetMapping("/users/users-revoked")
    public ResponseEntity<ApiUserNotAceptableResponse> getUserWithStatusNotAcetable(
            @RequestParam Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiUserNotAceptableResponse users = userProcessingService.findWithStatusNotAccepted(accountId, page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/correct-user")
    public ResponseEntity<?> correctUser( @RequestParam Long accountId,  @RequestParam String identificationNumber){
        try{
            ApiCorrectUser apiCorrectUser=userProcessingService.correctUser(accountId,identificationNumber, Message.PENDING);
            return ResponseEntity.ok(apiCorrectUser);
        }catch (GenericException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @PostMapping("/users/revoked-users-file")
    public ResponseEntity<?> postRevokeUsersByFile(@RequestParam MultipartFile file,@RequestParam Long accountId){
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío.");
        }
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos CSV (.csv)");
        }
        List<UserNotAcceptableResponse> revokedList;
        try{
            revokedList=userProcessingService.updateUsersByFile(file,accountId,true);
        } catch (IOException e) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al actualizar la lista de usuarios");
        }
        return ResponseEntity.ok(revokedList);
    }

    @PostMapping("/users/update-users-file")
    public ResponseEntity<?> updateRevokeUsersByFile(@RequestParam MultipartFile file,@RequestParam Long accountId){
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío.");
        }
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos CSV (.csv)");
        }
        List<UserNotAcceptableResponse> revokedList;
        try{
            revokedList=userProcessingService.updateUsersByFile(file,accountId,false);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al actualizar la lista de usuarios");
        }
        return ResponseEntity.ok(revokedList);
    }
    
    @GetMapping("/users/activos")
    public ResponseEntity<List<UserJsonPropertyDto>> getActiveUsers() {
        List<UserJsonPropertyDto> activeUsers = userBlockService.activeUsers()
            .stream()
            .toList();

        userBlockService.processActivateUsersByRegional(activeUsers);
        return ResponseEntity.ok(activeUsers);
    }


    @GetMapping("/users/cesantes")
    public ResponseEntity<List<UserJsonPropertyDto>> getCesantesUsers() {
        List<UserJsonPropertyDto> cesantes = userBlockService.cesantesUsers();
        userBlockService.processCesanteUsersByRegional(cesantes);
        return ResponseEntity.ok(cesantes);
    }



}