package com.comfandi.phobos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.ValidationDocument;
import com.comfandi.phobos.repository.ValidationDocumentRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;
import com.comfandi.phobos.service.dto.SubsanacionResponse;
import com.comfandi.phobos.service.dto.UserFixRequest;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SubsanacionService {

    @Autowired
    private ValidationResultRepository validationRepo;

    @Autowired
    private ExcelGeneracionService excelService;

    @Autowired
    private RapGeneracionService rapService;

    @Autowired
    private ValidationDocumentRepository documentRepo;


    public SubsanacionResponse procesarSubsanacion(List<UserFixRequest> usuarios) {

        Optional<ValidationDocument> ultimoExcelOpt = documentRepo.findFirstByFileTypeOrderByCreatedAtDesc("EXCEL");
    
        if (!ultimoExcelOpt.isPresent()) {
            throw new RuntimeException("No existe documento Excel previo para generar nueva versión.");
        }

        ValidationDocument ultimoExcel = ultimoExcelOpt.get();
        int nuevaVersion = ultimoExcel.getVersion() + 1;

        // El resto de tu código se mantiene igual...
        ultimoExcel.setEstadoDocumento("revisado");
        documentRepo.save(ultimoExcel);

        LocalDate fechaCorte = LocalDate.now();


        for (UserFixRequest u : usuarios) {

            List<String> estados = validationRepo.findEstados(
                    u.getDocumentType(),
                    u.getDocumentNumber()
            );

            if (estados == null || estados.isEmpty()) {
                throw new RuntimeException("No existe registro de validación para el usuario: " + u.getDocumentNumber());
            }

            String estadoActual = estados.get(0);

            if (!estadoActual.equalsIgnoreCase("inhabilitar")) {
                throw new RuntimeException(
                        "El usuario no está actualmente inhabilitado y no se puede subsanar: " + u.getDocumentNumber()
                );
            }

            validationRepo.updateEstadoFinalAndObservacion(
                    u.getDocumentType(),
                    u.getDocumentNumber(),
                    u.getNewEstadoFinal(),
                    u.getObservacion()
            );

            validationRepo.revalidateUser(
                    u.getDocumentType(),
                    u.getDocumentNumber()
            );
        }

        ValidationDocument nuevoExcel = excelService.generarExcelValidacion(fechaCorte);
        nuevoExcel.setVersion(nuevaVersion);
        nuevoExcel.setCutoffDate(fechaCorte);
        documentRepo.save(nuevoExcel);


        Map<String, ValidationDocument> rapFiles =
                rapService.generarArchivosRAP(fechaCorte);

        rapFiles.values().forEach(doc -> {
            doc.setVersion(nuevaVersion);
            doc.setCutoffDate(fechaCorte);
            documentRepo.save(doc);
        });

        return new SubsanacionResponse(nuevoExcel, rapFiles);
    }
}
