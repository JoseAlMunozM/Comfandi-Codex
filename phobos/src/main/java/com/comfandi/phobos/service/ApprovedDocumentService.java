package com.comfandi.phobos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.repository.ValidationDocumentRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class ApprovedDocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApprovedDocumentService.class);
    
    private final ValidationDocumentRepository documentoRepository;
    private final ValidationResultRepository validationResultRepository;
    
    @Value("${api.basic_auth_user:SYSTEM}")
    private String usuarioAutenticado;
    
    public ApprovedDocumentService(ValidationDocumentRepository documentoRepository,
                                    ValidationResultRepository validationResultRepository) {
        this.documentoRepository = documentoRepository;
        this.validationResultRepository = validationResultRepository;
    }
    
    public ValidationDocument aprobarDocumento(Long documentId) {
        logger.info("Solicitando aprobación de documento ID: {} por usuario: {}", documentId, usuarioAutenticado);
        
        try {
            ValidationDocument documento = documentoRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentId));
            
            validarPrecondicionesAprobacion(documento);
            
            documento.setEstadoDocumento("aprobado");
            documento.setApprovedBy(usuarioAutenticado);
            documento.setApprovedAt(LocalDateTime.now());
            
            ValidationDocument documentoAprobado = documentoRepository.save(documento);
            logger.info("Documento {} aprobado exitosamente por {}", documentId, usuarioAutenticado);
            
            marcarUsuariosComoValidados(documento.getCutoffDate(), documentId);
            
            registrarAuditoriaAprobacion(documentoAprobado);
            
            return documentoAprobado;
            
        } catch (Exception e) {
            logger.error("Error en transacción de aprobación para documento ID: {}", documentId, e);
            throw new RuntimeException("Error aprobando documento: " + e.getMessage());
        }
    }
    
    private void validarPrecondicionesAprobacion(ValidationDocument documento) {
        if (!"generado".equals(documento.getEstadoDocumento())) {
            throw new RuntimeException(
                String.format("El documento ID: %s no puede ser aprobado. Estado actual: %s. Solo se pueden aprobar documentos en estado 'generado'.",
                    documento.getDocumentId(), documento.getEstadoDocumento())
            );
        }
        
        if (documento.getCutoffDate() == null) {
            throw new RuntimeException("El documento no tiene fecha de corte asignada");
        }
        
        logger.debug("Precondiciones validadas para documento ID: {}", documento.getDocumentId());
    }
    
    private void marcarUsuariosComoValidados(LocalDate cutoffDate, Long documentId) {
        try {
            int usuariosActualizados = validationResultRepository.marcarUsuariosComoValidados(cutoffDate, documentId);
            logger.info("{} usuarios marcados como validados oficialmente para documento ID: {}", 
                       usuariosActualizados, documentId);
            
        } catch (Exception e) {
            logger.error("Error marcando usuarios como validados para documento ID: {}", documentId, e);
            throw new RuntimeException("Error actualizando estado de usuarios: " + e.getMessage());
        }
    }
    
    private void registrarAuditoriaAprobacion(ValidationDocument documento) {
        try {
            logger.info("AUDITORÍA - Documento {} aprobado por {} en {}", 
                       documento.getDocumentId(), usuarioAutenticado, LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("Error registrando auditoría de aprobación para documento ID: {}", 
                        documento.getDocumentId(), e);
        }
    }
    
    public ValidationDocument obtenerDocumento(Long documentId) {
        return documentoRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentId));
    }
    
    public boolean puedeSerAprobado(Long documentId) {
        try {
            ValidationDocument documento = documentoRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
            
            return "generado".equals(documento.getEstadoDocumento());
            
        } catch (Exception e) {
            return false;
        }
    }
    
    public long contarUsuariosDocumento(Long documentId) {
        try {
            ValidationDocument documento = documentoRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentId));
            
            return validationResultRepository.countUsuariosPorDocumento(documento.getCutoffDate(), documentId);
            
        } catch (Exception e) {
            logger.error("Error contando usuarios para documento ID: {}", documentId, e);
            return 0;
        }
    }
}