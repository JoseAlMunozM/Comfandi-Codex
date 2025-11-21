package com.comfandi.korlon.controller;

import com.comfandi.korlon.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotificarUsuarios() {
        LocalDate startDate = LocalDate.of(2025, 5, 25);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        String response = notificationController.notificarUsuarios(startDate, endDate);

        verify(notificationService, times(1)).notificarUsuariosInhabilitados(startDate, endDate);

        assertEquals("Notificaciones enviadas exitosamente", response);
    }
}
