package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiUserStateResponse {
    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;
    private String document;
    @JsonProperty("postulation_state")
    private String postulationState;

}

