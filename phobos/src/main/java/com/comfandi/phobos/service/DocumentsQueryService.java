package com.comfandi.phobos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.DocumentQuery;
import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.repository.ValidationDocumentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DocumentsQueryService {

 private static final Logger logger = LoggerFactory.getLogger(DocumentsQueryService.class);
    
    private final ValidationDocumentRepository documentoRepository;
    
    public DocumentsQueryService(ValidationDocumentRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }
    
    public List<DocumentQuery> consultarDocumentosPorCorte(LocalDate cutoffDate) {
        logger.info("Consultando documentos para fecha de corte: {}", cutoffDate);
        
        List<ValidationDocument> documentos = documentoRepository.findByCutoffDateOrderByCreatedAtDesc(cutoffDate);
        
        List<DocumentQuery> resultado = documentos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
            
        logger.info("Encontrados {} documentos para la fecha {}", resultado.size(), cutoffDate);
        return resultado;
    }
    
    public List<DocumentQuery> consultarTodosDocumentos() {
        logger.info("Consultando todos los documentos");
        
        List<ValidationDocument> documentos = documentoRepository.findAllByOrderByCutoffDateDescCreatedAtDesc();
        
        List<DocumentQuery> resultado = documentos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
            
        logger.info("Encontrados {} documentos en total", resultado.size());
        return resultado;
    }
    

    public List<DocumentQuery> consultarDocumentosAgrupados() {
        logger.info("Consultando documentos agrupados por fecha de corte");
        
        List<ValidationDocument> documentos = documentoRepository.findAllByOrderByCutoffDateDescCreatedAtDesc();
        
        return documentos.stream()
            .collect(Collectors.groupingBy(ValidationDocument::getCutoffDate))
            .values()
            .stream()
            .flatMap(list -> list.stream()
                .collect(Collectors.groupingBy(ValidationDocument::getFileType))
                .values()
                .stream()
                .map(fileTypeList -> fileTypeList.stream()
                    .max((d1, d2) -> d2.getVersion().compareTo(d1.getVersion()))
                    .orElse(null)
                )
                .filter(doc -> doc != null)
            )
            .map(this::convertirADTO)
            .sorted((d1, d2) -> d2.getFechaCorte().compareTo(d1.getFechaCorte()))
            .collect(Collectors.toList());
    }

    private DocumentQuery convertirADTO(ValidationDocument documento) {
        return new DocumentQuery(
            documento.getDocumentId(),
            documento.getCutoffDate(),
            documento.getVersion(),
            documento.getEstadoDocumento(),
            documento.getFileType(),
            documento.getS3Url(),
            documento.getCreatedAt(),
            documento.getCreatedBy(),
            documento.getApprovedBy(),
            documento.getApprovedAt()
        );
    }

    public List<Object[]> obtenerResumenDocumentos() {
        return documentoRepository.findResumenDocumentosPorCorte();
    }

}