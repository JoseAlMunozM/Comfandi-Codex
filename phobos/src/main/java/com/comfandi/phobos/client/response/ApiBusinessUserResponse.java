package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiBusinessUserResponse {

    @JsonProperty("document_abbreviation")
    private String documentType;
    @JsonProperty("document")
    private String document;
    @JsonProperty("business_name")
    private String businessName;
    @JsonProperty("business_id")
    private String businessId;
}
