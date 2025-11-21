package com.comfandi.phobos.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApiEmailNotificationRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("document_url")
    private String documentURL;
    @JsonProperty("document_name")
    private String documentName;
    @JsonProperty("file_type")
    private String type;
}
