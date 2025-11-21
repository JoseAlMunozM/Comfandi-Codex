package com.comfandi.korlon.manager.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoDatosPlanillaRow {

	    @JsonProperty("regional_cobro")
	    private String regional;

	    @JsonProperty("modalidad")
	    private String mode;

	    @JsonProperty("suma_de_valor")
	    private double sumValues;

	    @JsonProperty("cuenta_de_cedula")
	    private int totalPerson;

	    @JsonProperty("modalidad_resumida")
	    private String resumeMode;

	    @JsonProperty("cuenta_ingreso")
	    private String account;

	    @JsonProperty("cebe")
	    private String cebe;

	    @JsonProperty("texto_registro")
	    private String textoRegistry;

	    @JsonProperty("asignacion")
	    private String assignation;

	    @JsonProperty("longitud_texto")
	    private int textLength;

}
