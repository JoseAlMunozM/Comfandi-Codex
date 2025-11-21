package com.comfandi.phobos.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Message {

    /**billing account courses**/
    public static final String CHARGED = "Cobrado";
    public static final String NOT_CHARGED = "Sin Cobrar";
    public static final String PENDING= "Pendiente";
    public static final String CHARGED_UNDER_THREE_YEARS_DESCRIPTION = "dentro de los cursos en los que ya se ha inscrito esta el curso actual";
    public static final String DESCRIPTION_LAST_LOGIN = "Las fechas no son las correspondientes";
    public static final String DESCRIPTION_SISE_VALIDATION = "Validación SISE no aprobada";
    public static final String DESCRIPTION_DUPLICATE_PERSON = "Persona duplicada";
    public static final String DESCRIPTION_NON_REFERRED_PERSON = "Persona no remitida";
    public static final String NOT_CHARGED_REMISSION_START_DATES = "Fecha de remision y fecha de inicio de curso superior a 90 dias";
    public static final String NOT_CHARGED_START_DATES_ASSISTANT_BELLOW_30_PERCENT = "Porcentaje de asistencia inferior al 30% del curso sin terminar";
    public static final String NOT_CHARGED_START_DATES_ASSISTANT_BELLOW_80_PERCENT = "Porcentaje de asistencia inferior al 80% del curso terminado";
    public static final String NOT_CHARGED_NOT_ASSISTANCE = "Sin asistencia";

    //static variables
    public static final String PAYMENT_REGION="CALI";
    public static final String TEMPLATE_TYPE = "COBRO";
    public static final String CONTRIBUTING_OBSERVATION = "COTIZANTE";

    public static final String AVAILABLE= "HABILITADO";
    public static final String UNAVAILABLE= "INHABILITADO";
    public static final String NOT_AVAILABLE_ASSISTANT_BELLOW_80_PERCENT="Porcentaje de asistencia inferior al 80%";

    public static final String UNAVAILABLE_STATUS_FORMATION ="En Formacion";
    public static final String UNAVAILABLE_STATUS_IN_CALL ="Convocatoria";


    /**Billing account Workshops**/
    public static final String APPOINTMENT_NOT_FOUND="Cita no encontrada";
    public static final String APPOINTMENT_NOT_SUCCESS= "Cita No cumplida";
    public static final String APPOINTMENT_SUCCESS= "Cita cumplida";
    public static final String ORIENTATION_DATE_MORE_THAN_START_DATE="Fecha de orientacion mayor a la fecha de inicio";
    public static final String WORKSHOP_NOT_FOUND="Taller no encontrado";
    public static final String WORKSHOP_FOUND_90_DAYS = "Taller ya inscrito en los ultimos 90 dias";

}
