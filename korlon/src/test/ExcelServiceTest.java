package com.comfandi.korlon.services;

import com.comfandi.korlon.manager.data.BusinessData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest {

    @Mock
    private BusinessService businessService;

    @InjectMocks
    private ExcelService excelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateExcel() throws IOException {
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

        when(businessService.getAllBussiness()).thenReturn(businessList);

        byte[] excelBytes = excelService.generateExcel();

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        verify(businessService, times(1)).getAllBussiness();
    }
}