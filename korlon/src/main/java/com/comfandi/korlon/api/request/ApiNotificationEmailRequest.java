package com.comfandi.korlon.api.request;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApiNotificationEmailRequest {
    private String email;
    private List<ApiInformationDatabase> data;
}
