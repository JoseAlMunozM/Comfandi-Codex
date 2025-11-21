package com.comfandi.phobos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.service.ExcelGeneracionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/validacion")
public class ApiValidationDocument {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiValidationDocument.class);
    
    private final ExcelGeneracionService excelService;
    
    public ApiValidationDocument(ExcelGeneracionService excelService) {
        this.excelService = excelService;
    }
    
    @PostMapping("/generar-excel")
    public ResponseEntity<?> generarExcelValidacion(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        
        try {
            logger.info("Solicitud para generar Excel - Fecha: {}, Usuario: {}", cutoffDate);
            
            if (cutoffDate == null) {
                cutoffDate = LocalDate.now();
            }
    
            
            ValidationDocument documento = excelService.generarExcelValidacion(cutoffDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Archivo Excel generado exitosamente");
            response.put("data", documento);
            response.put("documentId", documento.getDocumentId());
            response.put("filePath", documento.getS3Url().replace("file://", ""));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error de validación: " + e.getMessage()));
                
        } catch (RuntimeException e) {
            logger.error("Error al generar Excel: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error al generar Excel: " + e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Error interno del servidor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error interno del servidor"));
        }
    }
    
    @GetMapping("/descargar-excel/{documentId}")
    public ResponseEntity<?> descargarExcel(@PathVariable Long documentId) {
        try {
            logger.info("Solicitud para descargar Excel - Document ID: {}", documentId);
            
            if (documentId == null || documentId <= 0) {
                return ResponseEntity.badRequest()
                    .body(crearErrorResponse("ID de documento inválido"));
            }
            
            org.springframework.core.io.Resource resource = excelService.descargarExcel(documentId);
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(resource);
                
        } catch (RuntimeException e) {
            logger.warn("Error al descargar Excel: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error al descargar archivo: " + e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Error interno al descargar Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error interno del servidor"));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Excel Generation Service");
        response.put("timestamp", LocalDate.now().toString());
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = "Parámetro inválido: " + ex.getName() + ". Se esperaba un formato de fecha YYYY-MM-DD";
        logger.warn("Error de tipo de parámetro: {}", error);
        return ResponseEntity.badRequest()
            .body(crearErrorResponse(error));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(crearErrorResponse(ex.getMessage()));
    }
    
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", mensaje);
        errorResponse.put("timestamp", LocalDate.now().toString());
        return errorResponse;
    }
}