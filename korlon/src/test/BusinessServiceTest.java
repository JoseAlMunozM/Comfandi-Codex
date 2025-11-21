package com.comfandi.korlon.services;

import com.comfandi.korlon.manager.data.BusinessData;
import com.comfandi.korlon.repositories.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private BusinessService businessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBussiness() {
        // Datos simulados
        BusinessData b1 = new BusinessData("12345", "Empresa 1", "CC123", "Juan Perez",
                "Calle 1", "1234567890", "Ciudad 1", "empresa1@mail.com",
                "Region 1", "Programa A", 1000.0, "Region Facturacion 1", "2025-01-01",
                "2025-02-01", 50.0, "2025-03-01", "2025-06-01", "Proveedor 1",
                "2025-01-05", "Tipo 1");

        BusinessData b2 = new BusinessData("67890", "Empresa 2", "CC456", "Ana Gomez",
                "Calle 2", "0987654321", "Ciudad 2", "empresa2@mail.com",
                "Region 2", "Programa B", 2000.0, "Region Facturacion 2", "2025-02-01",
                "2025-03-01", 30.0, "2025-04-01", "2025-07-01", "Proveedor 2",
                "2025-02-05", "Tipo 2");

        List<BusinessData> businessList = Arrays.asList(b1, b2);

        // Simulamos la respuesta del repositorio
        when(businessRepository.findAll()).thenReturn(businessList);

        // Llamada al servicio
        List<BusinessData> result = businessService.getAllBussiness();

        // Verificaciones
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Empresa 1", result.get(0).getBusinessName());
        assertEquals("Empresa 2", result.get(1).getBusinessName());

        // Verificamos que el repositorio fue llamado exactamente una vez
        verify(businessRepository, times(1)).findAll();
    }
}
