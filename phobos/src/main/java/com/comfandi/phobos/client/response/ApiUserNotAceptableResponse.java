package com.comfandi.phobos.client.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiUserNotAceptableResponse {
    @JsonProperty("data")
    private List<UserNotAcceptableResponse> users;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

