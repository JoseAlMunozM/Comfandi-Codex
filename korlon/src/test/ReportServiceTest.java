package com.comfandi.korlon.services;

import com.comfandi.korlon.manager.data.BusinessData;
import com.comfandi.korlon.repositories.BusinessRepository;
import net.sf.jasperreports.engine.JasperExportManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private BusinessRepository businessRepository;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        businessRepository = Mockito.mock(BusinessRepository.class);
        reportService = new ReportService(businessRepository);
    }

    @Test
    void testExportCuentaCobroPdf_generatesPdf() throws Exception {
        BusinessData b1 = new BusinessData();
        b1.setRegional("Cali");
        b1.setCebe("123");
        b1.setMode("Normal");
        b1.setCant(10L);
        b1.setSum(50000.0);
        b1.setPriceValue("50000");

        BusinessData b2 = new BusinessData();
        b2.setRegional("Bogotá");
        b2.setCebe("456");
        b2.setMode("Urgente");
        b2.setCant(5L);
        b2.setSum(175000.0);
        b2.setPriceValue("175000");

        List<BusinessData> mockData = Arrays.asList(b1, b2);
        when(businessRepository.findAll()).thenReturn(mockData);

        byte[] pdfBytes = reportService.exportCuentaCobroPdf();

        assertNotNull(pdfBytes, "El PDF generado no debe ser nulo");
        assertTrue(pdfBytes.length > 0, "El PDF debe tener contenido");

        try (FileOutputStream fos = new FileOutputStream("target/test-cuenta-cobro.pdf")) {
            fos.write(pdfBytes);
        }
    }
}
