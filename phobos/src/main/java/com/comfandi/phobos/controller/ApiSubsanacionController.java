package com.comfandi.phobos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.phobos.service.SubsanacionService;
import com.comfandi.phobos.service.dto.SubsanacionResponse;
import  com.comfandi.phobos.service.dto.UserFixRequest;

@RestController
@RequestMapping("/api/v1/subsanacion")
public class ApiSubsanacionController {
     private final SubsanacionService subsanacionService;

    public ApiSubsanacionController(SubsanacionService subsanacionService) {
        this.subsanacionService = subsanacionService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarSubsanacion(@RequestBody List<UserFixRequest> usuariosCorregidos) {

        if (usuariosCorregidos == null || usuariosCorregidos.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "La lista de usuarios corregidos no puede estar vacía"
            ));
        }

        try {
            SubsanacionResponse response = subsanacionService.procesarSubsanacion(usuariosCorregidos);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
