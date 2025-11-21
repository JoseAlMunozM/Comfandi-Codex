package com.comfandi.phobos.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class ApiInformationDatabase {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("nit")
    private String nit;

    @JsonProperty("empresa")
    private String companyName;

    @JsonProperty("cedula")
    private String identification;

    @JsonProperty("tipo_documento")
    private String tipoDocumento;

    @JsonProperty("nombre_completo")
    private String fullName;

    @JsonProperty("direccion")
    private String address;

    @JsonProperty("telefono")
    private String phoneNumber;

    @JsonProperty("ciudad")
    private String cityName;

    @JsonProperty("correo_electronico")
    private String emailAddress;

    @JsonProperty("nombre_del_programa")
    private String programName;

    @JsonProperty("modalidad")
    private String mode;

    @JsonProperty("valor")
    private String fee;

    @JsonProperty("regional_cobro")
    private String paymentRegion;

    @JsonProperty("regional_domicilio")
    private String paymentCityName;

    @JsonProperty("observacion_beneficiario_cotizante")
    private String contributingBeneficiaryObservation;

    @JsonProperty("numero_de_documento_de_cotizante")
    private String contributingIdNumber;

    @JsonProperty("categoria")
    private String category;

    @JsonProperty("fecha_validacion_inicial")
    private String startValidationDate;

    @JsonProperty("validacion_revalidacion_afiliacion_a_caja")
    private String revealValidationAffiliationToCash;

    @JsonProperty("validado_por")
    private String validatedBy;

    @JsonProperty("fecha_envio_bd_cobro")
    private String paymentSendingDate;

    @JsonProperty("porcentaje_avance_del_programa")
    private String programAdvancePercent;

    @JsonProperty("fecha_de_inicio_del_programa")
    private String startingProgramDate;

    @JsonProperty("fecha_de_finalizacion_del_programa")
    private String endingProgramDate;

    @JsonProperty("fecha_de_registro_sise")
    private String registrationDate;

    @JsonProperty("fecha_evaluacion_sise")
    private String siseEvaluationDate;

    @JsonProperty("proveedor")
    private String provider;

    @JsonProperty("fecha_remision")
    private String remissionDate;

    @JsonProperty("afiliada")
    private String affiliate;

    @JsonProperty("fecha_de_afiliacion")
    private String affiliationDate;

    @JsonProperty("fecha_validacion_afiliacion")
    private String merbershipValidationDate;

    @JsonProperty("estado_parafiscales")
    private String parafiscalStatus;

    @JsonProperty("fecha_validacion_parafiscales")
    private String parafiscalValidationDate;

    @JsonProperty("nombre_de_quien_realiza_la_validacion_afiliacion")
    private String membershipValidationPerson;

    @JsonProperty("nombre_de_quien_realiza_validacion_parafiscales")
    private String parafiscalValidationPerson;

    @JsonProperty("numero_colaboradores")
    private String collabNumber;

    @JsonProperty("tipo_de_plantilla")
    private String templateType;

    @JsonProperty("cobro")
    private String charge;

    @JsonProperty("observaciones")
    private String observations;

    @JsonProperty("program_id")
    private String programId;

    @JsonProperty("portfolio_id")
    private String portfolioId;
}
