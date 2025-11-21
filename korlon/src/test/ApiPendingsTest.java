package com.comfandi.korlon.controller;

import com.comfandi.korlon.api.response.ApiBillingAccountResponse;
import com.comfandi.korlon.services.PendingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiPendingsTest {

    @Mock
    private PendingsService pendingsService;

    @InjectMocks
    private ApiPendings apiPendings;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmpresasPendientesCobro() {
        BillingAccount account1 = new BillingAccount(1, "Empresa 1", "Tipo A", "Ley X", "wordUrl1", "excelUrl1", true, true);
        BillingAccount account2 = new BillingAccount(2, "Empresa 2", "Tipo B", "Ley Y", "wordUrl2", "excelUrl2", false, true);

        when(pendingsService.obtenerEmpresasPendientesCobro())
                .thenReturn(Arrays.asList(account1, account2));

        ResponseEntity<List<ApiBillingAccountResponse>> response = apiPendings.getEmpresasPendientesCobro();

        verify(pendingsService, times(1)).obtenerEmpresasPendientesCobro();
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("Empresa 1", response.getBody().get(0).getNombre());
        assertEquals("Empresa 2", response.getBody().get(1).getNombre());
    }
}
