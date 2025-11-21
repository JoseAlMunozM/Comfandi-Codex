package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiUserResidenceResponse {

    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;

    @JsonProperty("document")
    private String document;

    @JsonProperty("register_date")
    private String registerDate;

    @JsonProperty("city")
    private String regionalResidence;
}
