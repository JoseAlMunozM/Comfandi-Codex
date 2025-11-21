package com.comfandi.phobos.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiUserRequest {
    private String document;

    @JsonProperty("document_abbreviation")
    private String documentAbbreviation;
}
