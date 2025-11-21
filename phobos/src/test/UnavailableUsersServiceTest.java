package com.comfandi.phobos.service;

import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.mapper.UnavailableUserMapper;
import com.comfandi.phobos.repository.UnavailableUserRepository;
import com.comfandi.phobos.repository.entities.UnavailableUserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UnavailableUsersServiceTest {

    @Mock
    private UnavailableUserRepository unavailableUserRepository;

    @InjectMocks
    private UnavailableUsersService unavailableUsersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListUnavailableUsers() {
        // Fechas de prueba
        String startDate = "2025-08-01";
        String endDate = "2025-08-31";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Datos simulados del repositorio
        UnavailableUserEntity entity1 = new UnavailableUserEntity(1L, "User1", start, end);
        UnavailableUserEntity entity2 = new UnavailableUserEntity(2L, "User2", start, end);

        when(unavailableUserRepository.findByDates(start, end))
                .thenReturn(Arrays.asList(entity1, entity2));

        // Llamada al servicio
        List<ApiUnavailableUsersResponse> result = unavailableUsersService.listUnavailableUsers(startDate, endDate);

        // Verificaciones
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getUsername());
        assertEquals("User2", result.get(1).getUsername());

        // Verifica que el repositorio fue llamado correctamente
        verify(unavailableUserRepository, times(1)).findByDates(start, end);
    }
}
