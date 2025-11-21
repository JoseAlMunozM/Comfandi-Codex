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
public class InfoPlantillaContableRow {

    private int rowId;

	@JsonProperty("consecutivo_del_documento")
    private String consecutivoDelDocumento;

    @JsonProperty("fecha_documento")
    private String fechaDocumento;

    @JsonProperty("clase_documento")
    private String claseDocumento;

    @JsonProperty("sociedad")
    private String sociedad;

    @JsonProperty("fecha_contabilidad")
    private String fechaContabilidad;

    @JsonProperty("periodo")
    private String periodo;

    @JsonProperty("moneda")
    private String moneda;

    @JsonProperty("referencia")
    private String referencia;

    @JsonProperty("texto_cabecera_doc")
    private String textoCabeceraDoc;

    @JsonProperty("numero_de_apunte_contable_dentro_del_documento")
    private String numeroDeApunteContableDentroDelDocumento;

    @JsonProperty("clave_de_contabilidad")
    private String claveDeContabilidad;

    @JsonProperty("indicador_cme")
    private String indicadorCme;

    @JsonProperty("numero_de_deudor_cliente")
    private String numeroDeDeudorCliente;

    @JsonProperty("numero_de_acreedor_proveedor")
    private String numeroDeAcreedorProveedor;

    @JsonProperty("cuenta_de_mayor")
    private String cuentaDeMayor;
    
    @JsonProperty("valor_de_importe")
    private String valorDeImporte;

    @JsonProperty("indicador_de_impuestos_iva")
    private String indicadorDeImpuestosIva;

    @JsonProperty("fecha_valor")
    private String fechaValor;

    @JsonProperty("fecha_base_para_vencimiento")
    private String fechaBaseParaVencimiento;

    @JsonProperty("clave_de_condicion_de_pago")
    private String claveDeCondicionDePago;

    @JsonProperty("numero_de_asignacion")
    private String numeroDeAsignacion;

    @JsonProperty("texto")
    private String texto;

    @JsonProperty("texto_nota")
    private String textoNota;

    @JsonProperty("centro_de_beneficio")
    private String centroDeBeneficio;

    @JsonProperty("centro_de_costo")
    private String centroDeCosto;

    @JsonProperty("orden_co")
    private String ordenCo;

    @JsonProperty("clave_de_referencia_1")
    private String claveDeReferencia1;

    @JsonProperty("clave_de_referencia_2")
    private String claveDeReferencia2;

    @JsonProperty("clave_de_referencia_3")
    private String claveDeReferencia3;

    @JsonProperty("posicion_presupuestaria")
    private String posicionPresupuestaria;

    @JsonProperty("centro_gestor")
    private String centroGestor;

    @JsonProperty("area")
    private String area;

    @JsonProperty("fondo")
    private String fondo;

    @JsonProperty("program")
    private String programa;

    @JsonProperty("documento_presupuestal")
    private String documentoPresupuestal;

    @JsonProperty("indicador_de_retencion_de_impuesto")
    private String indicadorDeRetencionDeImpuesto;
}
