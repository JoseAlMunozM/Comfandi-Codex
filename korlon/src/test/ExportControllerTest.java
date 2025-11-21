package com.comfandi.korlon.test;

import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.comfandi.korlon.controller.ExportController;
import com.comfandi.korlon.services.ExcelService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(ExportController.class)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExcelService excelService;

    @Test
    void exportExcel_ShouldReturnExcelFile() throws Exception {
        byte[] mockExcel = "fake-excel-content".getBytes();
        when(excelService.generateExcel()).thenReturn(mockExcel);

        mockMvc.perform(get("/api/v1/business-excel"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=business.xlsx"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(mockExcel));
    }
}