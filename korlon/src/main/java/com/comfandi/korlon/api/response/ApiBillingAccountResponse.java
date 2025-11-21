package com.comfandi.korlon.api.response;

import lombok.Data;

@Data
public class ApiBillingAccountResponse {
    private Integer id;
    private String nombre;
    private String tipo;
    private String ley;
    private String documentWordUrl;
    private String documentExcelUrl;
    private Boolean amortizable;
    private Boolean complete;

    public ApiBillingAccountResponse(
            Integer id,
            String nombre,
            String tipo,
            String ley,
            String documentWordUrl,
            String documentExcelUrl,
            Boolean amortizable,
            Boolean complete
    ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ley = ley;
        this.documentWordUrl = documentWordUrl;
        this.documentExcelUrl = documentExcelUrl;
        this.amortizable = amortizable;
        this.complete = complete;
    }

}