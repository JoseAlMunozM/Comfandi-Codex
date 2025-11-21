package com.comfandi.korlon.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiDocumentEmailRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("document_url")
    private String documentURL;
    @JsonProperty("document_name")
    private String documentName;
    @JsonProperty("file_type")
    private String type;
}
