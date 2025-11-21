package com.comfandi.korlon.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.korlon.manager.data.UnavailableUsers;
import com.comfandi.korlon.services.NotificationService;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint para enviar notificacion de usuarios inhabilitados entre dos fechas.
     * Ejemplo de uso:
     * GET /notificaciones/inhabilitados?startDate=25-05-2025&endDate=30-06-2025
     */
    @PostMapping("/inhabilitados/notificar")
    public String notificarUsuarios(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        notificationService.notificarUsuariosInhabilitados(startDate, endDate);
        return "Notificaciones enviadas exitosamente";
    }
}