package com.comfandi.phobos.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.comfandi.phobos.entity.CourseValidated;
import com.comfandi.phobos.entity.FosfecStatusQuery;
import com.comfandi.phobos.entity.ValidationResult;
import com.comfandi.phobos.repository.CourseValidatedRepository;
import com.comfandi.phobos.repository.FosfecStatusQueryRepository;
import com.comfandi.phobos.repository.ValidationResultRepository;
import com.comfandi.phobos.service.dto.AppointmentQueryInformationResponse;
import com.comfandi.phobos.service.dto.AppointmentQueryRequest;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserFosfecResultDto;

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
        Map<String, CourseValidated> courseValidatedByKey = new HashMap<>();

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
            courseValidatedRepository
                    .findByTipoIdentificacionAndIdentificacion(
                            user.getIdentificationType(),
                            user.getIdentification()
                    )
                    .ifPresent(cv -> courseValidatedByKey.put(key, cv));
        }

        Map<String, AppointmentQueryInformationResponse> appointmentsByKey = callAppointmentsApi(result)
                .stream()
                .collect(Collectors.toMap(
                        r -> normalizeDocumentType(r.getDocumentAbbreviation()) + "-" + r.getDocument(),
                        r -> r,
                        (a, b) -> a
                ));

        for (UserFosfecResultDto dto : result) {
            String key = normalizeDocumentType(dto.getIdentificationType()) + "-" + dto.getIdentification();
            CourseValidated courseValidated = courseValidatedByKey.get(key);
            if (courseValidated == null) {
                continue;
            }

            AppointmentQueryInformationResponse appointment = appointmentsByKey.get(key);

            ValidationResult validation = new ValidationResult();
            validation.setUsuario(courseValidated);
            validation.setPrograma(resolveProgram(appointment, courseValidated));
            validation.setCursoEstandarizado(resolveCourseStandard(appointment, courseValidated));
            LocalDate validationDate = parseDate(appointment != null ? appointment.getLastAppointment() : null);
            validation.setFechavalidacion(validationDate != null ? validationDate : LocalDate.now());
            validation.setEstadofinal(dto.getEstadoFinal());
            validation.setObservacion(dto.getObservacion());
            validation.setCutoffdate(LocalDate.now());
            validation.setDocumentid(parseLong(dto.getIdentification()));

            validationResultRepository.save(validation);
        }

        return result;
    }

    

    public List<AppointmentQueryInformationResponse> callAppointmentsApi(List<UserFosfecResultDto> usuarios) {

        List<AppointmentQueryRequest> requestBody = mapToAppointmentRequest(usuarios) ;

        return webClientBuilder.build()
                .post()
                .uri("https://apifomento.subsidioscomfandi.com.co/appointments/query-information")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AppointmentQueryInformationResponse>>() {})
                .blockOptional()
                .orElse(List.of());
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
                        normalizeDocumentType(u.getIdentificationType()),
                        u.getIdentification()
                ))
                .collect(Collectors.toList());
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String resolveProgram(AppointmentQueryInformationResponse appointment, CourseValidated courseValidated) {
        if (appointment != null && appointment.getFormation() != null && !appointment.getFormation().isEmpty()) {
            return appointment.getFormation().get(0);
        }
        return courseValidated.getPrograma();
    }

    private String resolveCourseStandard(AppointmentQueryInformationResponse appointment, CourseValidated courseValidated) {
        if (appointment != null && appointment.getFormation() != null && appointment.getFormation().size() >= 2) {
            return appointment.getFormation().get(1);
        }
        if (appointment != null && appointment.getFormation() != null && !appointment.getFormation().isEmpty()) {
            return appointment.getFormation().get(0);
        }
        return courseValidated.getProgramaEstandarizado();
    }
}
