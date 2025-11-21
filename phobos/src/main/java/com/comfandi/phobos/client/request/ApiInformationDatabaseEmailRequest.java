package com.comfandi.phobos.client.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiInformationDatabaseEmailRequest {
    private String email;
    private List<ApiInformationDatabase> data;
}
