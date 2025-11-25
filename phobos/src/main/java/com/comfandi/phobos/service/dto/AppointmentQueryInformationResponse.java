package com.comfandi.phobos.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class AppointmentQueryInformationResponse {

    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;

    @JsonProperty("document")
    private String document;

    @JsonProperty("last_appointment")
    private String lastAppointment;

    @JsonProperty("approbation_date")
    private String approbationDate;

    @JsonProperty("formation")
    private List<String> formation;
}
