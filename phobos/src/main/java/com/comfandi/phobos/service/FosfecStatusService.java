package com.comfandi.phobos.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.comfandi.phobos.entity.CourseValidated;
import com.comfandi.phobos.entity.FosfecStatusQuery;
import com.comfandi.phobos.entity.ValidationResult;
import com.comfandi.phobos.repository.CourseValidatedRepository;
import com.comfandi.phobos.repository.FosfecStatusQueryRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;
import com.comfandi.phobos.service.dto.AppointmentQueryRequest;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserFosfecResultDto;
import com.comfandi.phobos.service.dto.AppointmentQueryRequest;

@Service
public class FosfecStatusService {

    @Autowired
    @Qualifier("customWebClientBuilder")
    private WebClient.Builder webClientBuilder;

    @Autowired
    private CourseValidatedRepository courseValidatedRepository;

    @Autowired
    private ValidationResultRepository validationResultRepository;

    @Autowired
    private FosfecStatusQueryRepository fosfecStatusQueryRepository;

    @Autowired
    private UserBlockService userBlockService;

    public List<UserFosfecResultDto> getUsersWithFosfecStatus() {
        List<UserDto> externalUsers = userBlockService.allUsers();
        List<FosfecStatusQuery> fosfecRecords = fosfecStatusQueryRepository.findAll();

        Map<String, FosfecStatusQuery> fosfecMap = fosfecRecords.stream()
                .collect(Collectors.toMap(
                        f -> normalizeDocumentType(f.getDocumentType()) + "-" + f.getDocumentNumber(),
                        f -> f
                ));

        List<UserFosfecResultDto> result = new ArrayList<>();

        for (UserDto user : externalUsers) {

            String normalizedType = normalizeDocumentType(user.getIdentificationType());
            String key = normalizedType + "-" + user.getIdentification();

            if (!fosfecMap.containsKey(key)) {
                continue;
            }

            LocalDate endDate = LocalDate.parse(user.getEndDate());
            long daysPassed = ChronoUnit.DAYS.between(endDate, LocalDate.now());

            double progress = 0;
            try {
                progress = Double.parseDouble(user.getProgress());
            } catch (Exception ignored) {}

            String estadoFinal;
            String observacion;

            if (daysPassed > 5) {
                if (progress >= 80) {
                    estadoFinal = "no inhabilitar";
                    observacion = "Curso finalizado hace más de 5 días y avance ≥ 80%.";
                } else {
                    estadoFinal = "inhabilitar";
                    observacion = "Curso finalizó hace más de 5 días y avance < 80%.";
                }
            } else {
                if (progress >= 80) {
                    estadoFinal = "no inhabilitar";
                    observacion = "Curso finalizado hace menos de 5 días y cumple avance.";
                } else {
                    estadoFinal = "pendiente";
                    observacion = "Curso finalizado hace menos de 5 días sin cumplir avance.";
                }
            }

            UserFosfecResultDto dto = UserFosfecResultDto.builder()
                    .identificationType(user.getIdentificationType())
                    .identification(user.getIdentification())
                    .fullName(user.getFullName())
                    .estadoFinal(estadoFinal)
                    .observacion(observacion)
                    .build();

            result.add(dto);

            CourseValidated courseValidated = courseValidatedRepository
                    .findByTipoIdentificacionAndIdentificacion(
                            user.getIdentificationType(),
                            user.getIdentification()
                    )
                    .orElse(null);

            if (courseValidated == null) {
                continue;
            }

            ValidationResult validation = new ValidationResult();
            validation.setUsuario(courseValidated);
            validation.setPrograma(courseValidated.getPrograma());
            validation.setCursoEstandarizado(courseValidated.getProgramaEstandarizado());
            validation.setFechavalidacion(LocalDate.now());
            validation.setEstadofinal(estadoFinal);
            validation.setObservacion(observacion);
            validation.setCutoffdate(LocalDate.now());
            validation.setDocumentid(Long.valueOf(user.getIdentification()));

            validationResultRepository.save(validation);
        }

        return result;
    }

    

    public Object callAppointmentsApi(List<UserFosfecResultDto> usuarios) {

        List<AppointmentQueryRequest> requestBody = mapToAppointmentRequest(usuarios) ;

        return webClientBuilder.build()
                .post()
                .uri("https://apifomento.subsidioscomfandi.com.co/appointments/query-information")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }



    private String normalizeDocumentType(String type) {
        if (type == null) return "";

        type = type.trim().toLowerCase();

        switch (type) {
            case "cedula":
            case "cc":
                return "CC";

            case "ti":
            case "tarjeta identidad":
            case "tarjeta_identidad":
                return "TI";

            case "pasaporte":
            case "pa":
                return "PA";

            default:
                return type.toUpperCase();
        }
    }

    public List<AppointmentQueryRequest> mapToAppointmentRequest(List<UserFosfecResultDto> users) {
        return users.stream()
                .map(u -> new AppointmentQueryRequest(
                        u.getIdentificationType(),
                        u.getIdentification()
                ))
                .collect(Collectors.toList());
    }
}