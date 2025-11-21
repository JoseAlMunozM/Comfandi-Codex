package com.comfandi.phobos.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.repository.ValidationDocumentRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RapGeneracionService {
 private static final Logger logger = LoggerFactory.getLogger(RapGeneracionService.class);
    
    private final ValidationResultRepository validationResultRepository;
    private final ValidationDocumentRepository documentoRepository;
    
    @Value("${app.file.output-directory:./documentos/generados}")
    private String outputDirectory;
    
    private static final String TIPO_DOCUMENTO_CC = "CC";
    private static final String IDENTIFICARAP1 = "1";
    private static final String FORMATO_RAP01 = "2,%s,%s,%s,%d,%d,%s";
    private static final String FORMATO_RAP02 = "2,%s,%s,0,0,0";
    
    public RapGeneracionService(ValidationResultRepository validationResultRepository,
                              ValidationDocumentRepository documentoRepository) {
        this.validationResultRepository = validationResultRepository;
        this.documentoRepository = documentoRepository;
    }
    
    public Map<String, ValidationDocument> generarArchivosRAP(LocalDate cutoffDate) {
        logger.info("Generando archivos RAP01 y RAP02 para fecha: {}", cutoffDate);
        
        if (!validationResultRepository.existsByCutoffdate(cutoffDate)) {
            throw new RuntimeException("No hay resultados de validación para la fecha: " + cutoffDate);
        }
        
        Map<String, ValidationDocument> documentos = new HashMap<>();
        
        try {
            ValidationDocument rap01 = generarRAP01(cutoffDate);
            documentos.put("RAP01", rap01);
            
            ValidationDocument rap02 = generarRAP02(cutoffDate);
            documentos.put("RAP02", rap02);
            
            logger.info("Archivos RAP generados exitosamente - RAP01: {}, RAP02: {}", 
                       rap01.getDocumentId(), rap02.getDocumentId());
            
        } catch (Exception e) {
            logger.error("Error generando archivos RAP: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando archivos RAP: " + e.getMessage(), e);
        }
        
        return documentos;
    }

    private ValidationDocument generarRAP01(LocalDate cutoffDate) {
        logger.info("Generando RAP01 para fecha: {}", cutoffDate);
        
        List<Object[]> datos = validationResultRepository.findDatosParaRAP(cutoffDate);
        
        if (datos.isEmpty()) {
            throw new RuntimeException("No hay datos para generar RAP01 para la fecha: " + cutoffDate);
        }
        
        List<String> lineas = new ArrayList<>();
        for (Object[] dato : datos) {
            String identificacion = getSafeString(dato[0]);
            String tipoIdentificacion = getSafeString(dato[1]);
            String estadoFinal = getSafeString(dato[2]);
            
            String linea = formatearLineaRAP01(identificacion, tipoIdentificacion, estadoFinal, cutoffDate);
            lineas.add(linea);
        }
        
        String nombreArchivo = String.format("RAP01%s.txt", 
            cutoffDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        File archivo = generarArchivoTexto(lineas, nombreArchivo);
        
        return registrarDocumento(cutoffDate, archivo.getAbsolutePath(), "RAP01", lineas.size());
    }

    private ValidationDocument generarRAP02(LocalDate cutoffDate) {
        logger.info("Generando RAP02 para fecha: {}", cutoffDate);
        
        List<Object[]> datos = validationResultRepository.findTodosUsuariosCorte(cutoffDate);
        
        if (datos.isEmpty()) {
            throw new RuntimeException("No hay datos para generar RAP02 para la fecha: " + cutoffDate);
        }
        
        List<String> lineas = new ArrayList<>();
        for (Object[] dato : datos) {
            String identificacion = getSafeString(dato[0]);
            String tipoIdentificacion = getSafeString(dato[1]);
            
            String linea = formatearLineaRAP02(identificacion, tipoIdentificacion);
            lineas.add(linea);
        }
        
        String nombreArchivo = String.format("RAP02%s.txt", 
            cutoffDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        File archivo = generarArchivoTexto(lineas, nombreArchivo);
        
        return registrarDocumento(cutoffDate, archivo.getAbsolutePath(), "RAP02", lineas.size());
    }

    private String formatearLineaRAP01(String identificacion, String tipoIdentificacion, 
                                     String estadoFinal, LocalDate cutoffDate) {
        String tipoDoc = normalizarTipoDocumento(tipoIdentificacion);
        
        int campo4, campo5;
        if ("inhabilitar".equalsIgnoreCase(estadoFinal)) {
            campo4 = 1;
            campo5 = 1;
        } else {
            campo4 = 0;
            campo5 = 0;
        }
    
        String fechaFormateada = cutoffDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        
        return String.format(FORMATO_RAP01, tipoDoc, identificacion, IDENTIFICARAP1, 
                           campo4, campo5, fechaFormateada);
    }
    
    private String formatearLineaRAP02(String identificacion, String tipoIdentificacion) {
        String tipoDoc = normalizarTipoDocumento(tipoIdentificacion);
        return String.format(FORMATO_RAP02, tipoDoc, identificacion);
    }
    
    private String normalizarTipoDocumento(String tipoIdentificacion) {
        if (tipoIdentificacion == null) {
            return TIPO_DOCUMENTO_CC;
        }
        
        return switch (tipoIdentificacion.toUpperCase()) {
            case "CC", "CÉDULA", "CEDULA" -> "CC";
            case "CE", "CÉDULA EXTRANJERÍA", "CEDULA EXTRANJERIA" -> "CE";
            case "TI", "TARJETA IDENTIDAD" -> "TI";
            case "PASAPORTE", "PS" -> "PS";
            default -> TIPO_DOCUMENTO_CC;
        };
    }
    private File generarArchivoTexto(List<String> lineas, String nombreArchivo) {
        try {
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File archivo = new File(directory, nombreArchivo);
            
            try (FileWriter writer = new FileWriter(archivo);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }
            
            logger.info("Archivo generado: {} con {} líneas", archivo.getAbsolutePath(), lineas.size());
            return archivo;
            
        } catch (IOException e) {
            throw new RuntimeException("Error generando archivo de texto: " + e.getMessage(), e);
        }
    }
    
    private ValidationDocument registrarDocumento(LocalDate cutoffDate, String filePath, 
                                                String fileType, int cantidadRegistros) {
        Integer version = documentoRepository
            .findTopByCutoffDateAndFileTypeOrderByVersionDesc(cutoffDate, fileType)
            .map(doc -> doc.getVersion() + 1)
            .orElse(1);
        
        ValidationDocument documento = new ValidationDocument();
        documento.setCutoffDate(cutoffDate);
        documento.setFileType(fileType);
        documento.setS3Url("file://" + filePath);
        documento.setCreatedBy("SISTEMA");
        documento.setCreatedAt(LocalDateTime.now());
        documento.setVersion(version);
        documento.setEstadoDocumento("generado");
        
        ValidationDocument saved = documentoRepository.save(documento);
        logger.info("Documento {} registrado con ID: {}, registros: {}", 
                   fileType, saved.getDocumentId(), cantidadRegistros);
        
        return saved;
    }

    public List<ValidationDocument> listarDocumentosRAP(LocalDate cutoffDate) {
        logger.info("Listando documentos RAP para fecha: {}", cutoffDate);
        return documentoRepository.findRapDocumentsByCutoffDate(cutoffDate);
    }

    public ValidationDocument obtenerDocumentoPorId(Long documentId) {
        return documentoRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentId));
    }

    public Resource descargarDocumentoRAP(Long documentId) {
        ValidationDocument documento = obtenerDocumentoPorId(documentId);
        
        String filePath = documento.getS3Url().replace("file://", "");
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new RuntimeException("Archivo no encontrado: " + filePath);
        }
        
        return new FileSystemResource(file);
    }
    
    private String getSafeString(Object value) {
        return value != null ? value.toString().trim() : "";
    }

    public boolean validarFormatoRAP01(String contenido) {
        String[] lineas = contenido.split("\n");
        for (String linea : lineas) {
            if (!linea.matches("^2,[A-Z]{2},\\d+,1,[01],[01],\\d{8}$")) {
                return false;
            }
        }
        return true;
    }
    
    public boolean validarFormatoRAP02(String contenido) {
        String[] lineas = contenido.split("\n");
        for (String linea : lineas) {
            if (!linea.matches("^2,[A-Z]{2},\\d+,0,0,0$")) {
                return false;
            }
        }
        return true;
    }
}