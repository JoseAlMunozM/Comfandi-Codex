package com.comfandi.phobos.client.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiPostulationUserRequest {

    private List<ApiUserRequest> documents;
}
