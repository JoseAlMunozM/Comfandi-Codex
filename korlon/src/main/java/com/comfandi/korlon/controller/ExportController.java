package com.comfandi.korlon.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.korlon.services.ExcelService;
@RestController
@RequestMapping("/api/v1")
public class ExportController {

    private final ExcelService excelService;

     public ExportController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/business-excel")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] excel = excelService.generateExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=business.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }


}