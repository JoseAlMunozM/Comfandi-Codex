package com.comfandi.phobos.client.response;

import com.comfandi.phobos.service.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    @JsonProperty("data")
    private List<UserDto> data;

    @JsonProperty("ExistUser")
    private Boolean existUser;

}