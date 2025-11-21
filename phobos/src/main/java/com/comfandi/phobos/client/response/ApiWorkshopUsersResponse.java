package com.comfandi.phobos.client.response;

import com.comfandi.phobos.service.dto.WorkshopUserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiWorkshopUsersResponse {

    @JsonProperty("data")
    private List<WorkshopUserDto> data;
}
