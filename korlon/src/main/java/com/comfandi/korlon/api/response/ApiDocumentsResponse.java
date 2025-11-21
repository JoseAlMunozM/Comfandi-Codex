package com.comfandi.korlon.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ApiDocumentsResponse {
	
    private String status;
    private String excelDocumentUrl;
    private String pdfDocumentUrl;

}
