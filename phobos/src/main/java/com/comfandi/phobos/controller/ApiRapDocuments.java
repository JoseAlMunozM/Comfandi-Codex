package com.comfandi.phobos.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.service.RapGeneracionService;

@RestController
@RequestMapping("/api/v1/rap")
public class ApiRapDocuments {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiRapDocuments.class);
    
    private final RapGeneracionService rapService;
    
    public ApiRapDocuments(RapGeneracionService rapService) {
        this.rapService = rapService;
    }
    
    @PostMapping("/generar")
    public ResponseEntity<?> generarArchivosRAP(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        
        try {
            logger.info("Solicitud para generar archivos RAP - Fecha: {}", cutoffDate);
            
            // Validaciones
            if (cutoffDate == null) {
                return ResponseEntity.badRequest()
                    .body(crearErrorResponse("El parámetro 'cutoffDate' es requerido"));
            }
            
            if (cutoffDate.isAfter(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body(crearErrorResponse("La fecha de corte no puede ser futura"));
            }
            
            // Generar archivos
            Map<String, ValidationDocument> documentos = rapService.generarArchivosRAP(cutoffDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Archivos RAP01 y RAP02 generados exitosamente");
            response.put("data", documentos);
            response.put("cutoffDate", cutoffDate.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error generando archivos RAP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error generando archivos RAP: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error interno del servidor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error interno del servidor"));
        }
    }
    
    @GetMapping("/documentos/{cutoffDate}")
    public ResponseEntity<?> listarDocumentosRAP(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        
        try {
            List<ValidationDocument> documentos = rapService.listarDocumentosRAP(cutoffDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", documentos);
            response.put("count", documentos.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error listando documentos RAP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error listando documentos RAP: " + e.getMessage()));
        }
    }
    
    @GetMapping("/descargar/{documentId}")
    public ResponseEntity<?> descargarDocumentoRAP(@PathVariable Long documentId) {
        try {
            org.springframework.core.io.Resource resource = rapService.descargarDocumentoRAP(documentId);
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .header("Content-Type", "text/plain")
                .body(resource);
                
        } catch (RuntimeException e) {
            logger.error("Error descargando documento RAP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error descargando documento: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", mensaje);
        errorResponse.put("timestamp", LocalDate.now().toString());
        return errorResponse;
    }
}