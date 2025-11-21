package com.comfandi.korlon.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ApiDocumentNotificationResponse {
    private String document;
    private String status;
    private String error;
}
