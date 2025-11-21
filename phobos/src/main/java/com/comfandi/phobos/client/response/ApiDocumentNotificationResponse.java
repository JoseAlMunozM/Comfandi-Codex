package com.comfandi.phobos.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiDocumentNotificationResponse {
    private String document;
    private String status;
    private String error;
}
