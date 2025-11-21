package com.comfandi.phobos.service.dto;

import lombok.Data;

@Data
public class AppointmentQueryResponse {
    private String document;
    private String document_abbreviation;
    private String appointmentDate;
    private String status;
}
