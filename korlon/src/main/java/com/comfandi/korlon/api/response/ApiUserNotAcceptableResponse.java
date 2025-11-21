package com.comfandi.korlon.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiUserNotAcceptableResponse {

    @JsonProperty("Account")
    private String account;
    @JsonProperty("Date")
    private String date;
    @JsonProperty("Identification_number")
    private String identification;
    @JsonProperty("Full_name")
    private String fullName;
    @JsonProperty("Reason")
    private String reason;
    @JsonProperty("Status")
    private String status;
}
