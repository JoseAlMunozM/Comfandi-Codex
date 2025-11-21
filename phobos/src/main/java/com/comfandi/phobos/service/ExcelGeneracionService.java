package com.comfandi.phobos.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.repository.ValidationDocumentRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;

@Service
@Transactional
public class ExcelGeneracionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelGeneracionService.class);
    
    private final ValidationResultRepository validationResultRepository;
    private final ValidationDocumentRepository documentoRepository;
    
    
    
    @Value("${app.file.output-directory:./documentos/generados}")
   // @Value("${S3URL:./documentos/generados}")
    private String outputDirectory;
    
    public ExcelGeneracionService(ValidationResultRepository validationResultRepository,
                                ValidationDocumentRepository documentoRepository) {
        this.validationResultRepository = validationResultRepository;
        this.documentoRepository = documentoRepository;
    }
    
    public ValidationDocument generarExcelValidacion(LocalDate cutoffDate) {
        logger.info("Generando Excel de validación para fecha: {}", cutoffDate);
        
        if (!validationResultRepository.existsByCutoffdate(cutoffDate)) {
            throw new RuntimeException("No hay resultados de validación para la fecha: " + cutoffDate);
        }
        
        List<Object[]> resultados = validationResultRepository.findResultadosConDatosUsuario(cutoffDate);
        logger.info("Encontrados {} resultados para la fecha {}", resultados.size(), cutoffDate);
        
        File excelFile = generarArchivoExcel(resultados, cutoffDate);
        logger.info("Archivo Excel generado: {}", excelFile.getAbsolutePath());
        
        ValidationDocument documento = registrarDocumento(cutoffDate, excelFile.getAbsolutePath());
        logger.info("Documento registrado en BD con ID: {}", documento.getDocumentId());
        
        validationResultRepository.actualizarDocumentIdPorCutoffDate(cutoffDate, documento.getDocumentId());
        logger.info("Documento asociado a {} usuarios", resultados.size());
        
        return documento;
    }
    
    private File generarArchivoExcel(List<Object[]> resultados, LocalDate cutoffDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            Sheet sheet = workbook.createSheet("Usuarios Validados");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre", "Cédula", "Programa Estandarizado", "Estado Final", "Observación"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1;
            for (Object[] resultado : resultados) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(getSafeString(resultado[0])); // nombre
                row.createCell(1).setCellValue(getSafeString(resultado[1])); // cedula
                row.createCell(2).setCellValue(getSafeString(resultado[2])); // programa
                row.createCell(3).setCellValue(getSafeString(resultado[3])); // estado
                row.createCell(4).setCellValue(getSafeString(resultado[4])); // observacion
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                logger.info("Directorio creado: {}, éxito: {}", directory.getAbsolutePath(), created);
            }
            
            String fileName = String.format("usuarios_validados_%s.xlsx", 
                cutoffDate.format(DateTimeFormatter.BASIC_ISO_DATE));
            File excelFile = new File(directory, fileName);
            
            try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
            }
            
            return excelFile;
            
        } catch (IOException e) {
            logger.error("Error generando archivo Excel", e);
            throw new RuntimeException("Error generando archivo Excel", e);
        }
    }
    
    private String getSafeString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private ValidationDocument registrarDocumento(LocalDate cutoffDate, String filePath) {
        Integer version = documentoRepository
            .findTopByCutoffDateAndFileTypeOrderByVersionDesc(cutoffDate, "EXCEL")
            .map(doc -> doc.getVersion() + 1)
            .orElse(1);
        
        ValidationDocument documento = new ValidationDocument();
        documento.setCutoffDate(cutoffDate);
        documento.setFileType("EXCEL");
        documento.setS3Url("file://" + filePath);
        documento.setCreatedBy("Usuarios validados");
        documento.setCreatedAt(LocalDateTime.now());
        documento.setVersion(version);
        documento.setEstadoDocumento("generado");
        
        return documentoRepository.save(documento);
    }
    
    public Resource descargarExcel(Long documentId) {
        ValidationDocument documento = documentoRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentId));
        
        String filePath = documento.getS3Url().replace("file://", "");
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new RuntimeException("Archivo no encontrado: " + filePath);
        }
        
        return new FileSystemResource(file);
    }
}