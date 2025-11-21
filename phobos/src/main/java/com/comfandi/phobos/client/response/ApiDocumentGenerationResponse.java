package com.comfandi.phobos.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiDocumentGenerationResponse {
    private String status;
    private String excelDocumentUrl;
    private String pdfDocumentUrl;
}
