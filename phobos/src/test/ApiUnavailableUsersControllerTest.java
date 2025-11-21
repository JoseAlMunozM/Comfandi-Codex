package com.comfandi.phobos.controller;

import com.comfandi.phobos.service.UnavailableUsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiUnavailableUsersControllerTest {

    @Mock
    private UnavailableUsersService unavailableUsersService;

    @InjectMocks
    private ApiUnavailableUsersController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUnavailableUsers() {
        // Datos simulados
        List<String> users = Arrays.asList("user1", "user2", "user3");

        // Simulamos el servicio
        when(unavailableUsersService.listUnavailableUsers("2025-08-01", "2025-08-31"))
                .thenReturn(users);

        // Llamada al controlador
        ResponseEntity<?> response = controller.findUnavailableUsers("2025-08-01", "2025-08-31");

        // Verificaciones
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());

        // Verificamos que el servicio fue llamado con los parámetros correctos
        verify(unavailableUsersService, times(1))
                .listUnavailableUsers("2025-08-01", "2025-08-31");
    }
}
