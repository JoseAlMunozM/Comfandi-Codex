package com.comfandi.korlon.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiInformationDatabase {
    @JsonProperty("userId")
    private Long userId;

	@JsonProperty("nit")
    private String nit;

    @JsonProperty("empresa")
    private String empresa;

    @JsonProperty("cedula")
    private String cedula;

    @JsonProperty("tipo_documento")
    private String tipoDocumento;

    @JsonProperty("nombre_completo")
    private String nombreCompleto;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("ciudad")
    private String ciudad;

    @JsonProperty("correo_electronico")
    private String correoElectronico;

    @JsonProperty("nombre_del_programa")
    private String nombreDelPrograma;

    @JsonProperty("modalidad")
    private String modalidad;

    @JsonProperty("valor")
    private String valor;

    @JsonProperty("regional_cobro")
    private String regionalCobro;

    @JsonProperty("regional_domicilio")
    private String regionalDomicilio;

    @JsonProperty("observacion_beneficiario_cotizante")
    private String observacionBeneficiarioCotizante;

    @JsonProperty("numero_de_documento_de_cotizante")
    private String numeroDeDocumentoDeCotizante;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("fecha_validacion_inicial")
    private String fechaValidacionInicial;

    @JsonProperty("validacion_revalidacion_afiliacion_a_caja")
    private String validacionRevalidacionAfiliacionACaja;

    @JsonProperty("validado_por")
    private String validadoPor;

    @JsonProperty("fecha_envio_bd_cobro")
    private String fechaEnvioBdCobro;

    @JsonProperty("porcentaje_avance_del_programa")
    private String porcentajeAvanceDelPrograma;

    @JsonProperty("fecha_de_inicio_del_programa")
    private String fechaDeInicioDelPrograma;

    @JsonProperty("fecha_de_finalizacion_del_programa")
    private String fechaDeFinalizacionDelPrograma;

    @JsonProperty("fecha_de_registro_sise")
    private String fechaDeRegistroSise;

    @JsonProperty("fecha_evaluacion_sise")
    private String fechaEvaluacionSise;

    @JsonProperty("proveedor")
    private String proveedor;

    @JsonProperty("fecha_remision")
    private String fechaRemision;

    @JsonProperty("afiliada")
    private String afiliada;

    @JsonProperty("fecha_de_afiliacion")
    private String fechaDeAfiliacion;

    @JsonProperty("fecha_validacion_afiliacion")
    private String fechaValidacionAfiliacion;

    @JsonProperty("estado_parafiscales")
    private String estadoParafiscales;

    @JsonProperty("fecha_validacion_parafiscales")
    private String fechaValidacionParafiscales;

    @JsonProperty("nombre_de_quien_realiza_la_validacion_afiliacion")
    private String nombreDeQuienRealizaLaValidacionAfiliacion;

    @JsonProperty("nombre_de_quien_realiza_validacion_parafiscales")
    private String nombreDeQuienRealizaValidacionParafiscales;

    @JsonProperty("numero_colaboradores")
    private String numeroColaboradores;

    @JsonProperty("tipo_de_plantilla")
    private String tipoDePlantilla;

    @JsonProperty("cobro")
    private String charge;

    @JsonProperty("observaciones")
    private String observations;

    @JsonProperty("law")
    private String law;

    @JsonProperty("program_id")
    private String programId;

    @JsonProperty("portfolio_id")
    private String portfolioId;

}
