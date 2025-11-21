package com.comfandi.phobos.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentQueryRequest {
    private String document_abbreviation;
    private String document;
}
