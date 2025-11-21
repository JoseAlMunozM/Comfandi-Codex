package com.comfandi.phobos.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.comfandi.phobos.entity.DocumentQuery;
import com.comfandi.phobos.service.DocumentsQueryService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/consultas")
public class ApiDocumentsQuery {

     private static final Logger logger = LoggerFactory.getLogger(ApiDocumentsQuery.class);
    
    private final DocumentsQueryService consultaService;
    
    public ApiDocumentsQuery(DocumentsQueryService consultaService) {
        this.consultaService = consultaService;
    }
    
    /**
     * Consulta documentos por fecha de corte
     */
    @GetMapping("/documentos")
    public ResponseEntity<?> consultarDocumentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        
        try {
            logger.info("Consulta de documentos - cutoffDate: {}", cutoffDate);
            
            List<DocumentQuery> documentos;
            
            if (cutoffDate != null) {
                // Filtrar por fecha de corte específica
                documentos = consultaService.consultarDocumentosPorCorte(cutoffDate);
            } else {
                // Listar todos los documentos
                documentos = consultaService.consultarTodosDocumentos();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", documentos);
            response.put("count", documentos.size());
            response.put("filtroCutoffDate", cutoffDate);
            response.put("timestamp", LocalDate.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error consultando documentos: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error consultando documentos: " + e.getMessage()));
        }
    }
    
    /**
     * Consulta documentos agrupados por fecha de corte (última versión de cada tipo)
     */
    @GetMapping("/documentos/agrupados")
    public ResponseEntity<?> consultarDocumentosAgrupados() {
        try {
            logger.info("Consulta de documentos agrupados por fecha de corte");
            
            List<DocumentQuery> documentos = consultaService.consultarDocumentosAgrupados();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", documentos);
            response.put("count", documentos.size());
            response.put("timestamp", LocalDate.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error consultando documentos agrupados: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error consultando documentos agrupados: " + e.getMessage()));
        }
    }
    
    /**
     * Consulta resumen de documentos por fecha de corte
     */
    @GetMapping("/documentos/resumen")
    public ResponseEntity<?> consultarResumenDocumentos() {
        try {
            logger.info("Consulta de resumen de documentos");
            
            List<Object[]> resumen = consultaService.obtenerResumenDocumentos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", resumen);
            response.put("count", resumen.size());
            response.put("timestamp", LocalDate.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error consultando resumen de documentos: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error consultando resumen de documentos: " + e.getMessage()));
        }
    }
    
    /**
     * Consulta documentos por rango de fechas
     */
    @GetMapping("/documentos/rango")
    public ResponseEntity<?> consultarDocumentosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        try {
            logger.info("Consulta de documentos por rango: {} a {}", fechaInicio, fechaFin);
            
            // Validar rango de fechas
            if (fechaInicio.isAfter(fechaFin)) {
                return ResponseEntity.badRequest()
                    .body(crearErrorResponse("La fecha de inicio no puede ser posterior a la fecha fin"));
            }
            
            // En una implementación real, agregarías este método al servicio y repository
            // List<DocumentoConsultaDTO> documentos = consultaService.consultarDocumentosPorRango(fechaInicio, fechaFin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Endpoint en desarrollo - consulta por rango de fechas");
            response.put("fechaInicio", fechaInicio);
            response.put("fechaFin", fechaFin);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error consultando documentos por rango: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error consultando documentos por rango: " + e.getMessage()));
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