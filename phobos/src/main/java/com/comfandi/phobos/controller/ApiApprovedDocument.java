package com.comfandi.phobos.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.service.ApprovedDocumentService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documentos")
public class ApiApprovedDocument {

 private static final Logger logger = LoggerFactory.getLogger(ApiApprovedDocument.class);
    
    private final ApprovedDocumentService aprobacionService;
    
    public ApiApprovedDocument(ApprovedDocumentService aprobacionService) {
        this.aprobacionService = aprobacionService;
    }
    
    @PostMapping("/{documentId}/aprobar")
    public ResponseEntity<?> aprobarDocumento(@PathVariable Long documentId) {
        try {
            logger.info("Solicitud de aprobación para documento ID: {}", documentId);
            
            if (documentId == null || documentId <= 0) {
                return ResponseEntity.badRequest()
                    .body(crearErrorResponse("ID de documento inválido"));
            }
            
            ValidationDocument documentoAprobado = aprobacionService.aprobarDocumento(documentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento aprobado exitosamente");
            response.put("data", crearDocumentoResponse(documentoAprobado));
            response.put("usuariosActualizados", aprobacionService.contarUsuariosDocumento(documentId));
            
            logger.info("Documento {} aprobado exitosamente", documentId);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error aprobando documento {}: {}", documentId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error aprobando documento: " + e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Error interno aprobando documento {}: {}", documentId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(crearErrorResponse("Error interno del servidor"));
        }
    }
    
    @GetMapping("/{documentId}/estado")
    public ResponseEntity<?> consultarEstadoDocumento(@PathVariable Long documentId) {
        try {
            ValidationDocument documento = aprobacionService.obtenerDocumento(documentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", crearDocumentoResponse(documento));
            response.put("puedeSerAprobado", aprobacionService.puedeSerAprobado(documentId));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error consultando documento: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{documentId}/puede-aprobar")
    public ResponseEntity<?> puedeSerAprobado(@PathVariable Long documentId) {
        try {
            boolean puedeAprobar = aprobacionService.puedeSerAprobado(documentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentId", documentId);
            response.put("puedeSerAprobado", puedeAprobar);
            
            if (!puedeAprobar) {
                ValidationDocument documento = aprobacionService.obtenerDocumento(documentId);
                response.put("estadoActual", documento.getEstadoDocumento());
                response.put("mensaje", "Solo se pueden aprobar documentos en estado 'generado'");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(crearErrorResponse("Error verificando estado del documento"));
        }
    }
    
    private Map<String, Object> crearDocumentoResponse(ValidationDocument documento) {
        Map<String, Object> docResponse = new HashMap<>();
        docResponse.put("documentId", documento.getDocumentId());
        docResponse.put("cutoffDate", documento.getCutoffDate());
        docResponse.put("fileType", documento.getFileType());
        docResponse.put("estado", documento.getEstadoDocumento());
        docResponse.put("version", documento.getVersion());
        docResponse.put("s3Url", documento.getS3Url());
        docResponse.put("creadoPor", documento.getCreatedBy());
        docResponse.put("fechaCreacion", documento.getCreatedAt());
        docResponse.put("aprobadoPor", documento.getApprovedBy());
        docResponse.put("fechaAprobacion", documento.getApprovedAt());
        return docResponse;
    }
    
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", mensaje);
        errorResponse.put("timestamp", LocalDate.now().toString());
        return errorResponse;
    }
}