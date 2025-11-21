package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiWorkshopAppointmentsResponse {

    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;
    private String document;

    @JsonProperty("appointment_date")
    private String appointmentDate;

    @JsonProperty("appointment_status")
    private String statusAppointment;
}
