package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiUnavailableUserStatusResponse {
    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;
    private String document;

    @JsonProperty("status")
    private String status;
}
