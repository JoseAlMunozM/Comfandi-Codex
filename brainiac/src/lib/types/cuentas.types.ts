import { CUENTA_COBRO_STATUS, USER_ROLE } from "../config";


export type CuentaCobroStatus = keyof typeof CUENTA_COBRO_STATUS;

/*
export interface CuentaCobro{
    id:string;
    tipoDeCuenta: string;
    ley: string;
    numPersonas:string;
    total:string;
    estado: CuentaCobroStatus;
    analista: CuentaCobroStatus;
    jefatura: CuentaCobroStatus;
    xml: string;
    doc:string;
    createdAt:string;
}
*/

export enum TIPO_CUENTA_COBRO{
    UNEMPLOYED="Cesantes",
    EMPLOYABILITY="Empleabilidad",
}

export interface CuentaCobro{
    billingAccountId: string;
    billing_account_type: TIPO_CUENTA_COBRO;
    billing_account_name: string;
    creation_date: string;
    document_word_url: string | null;
    document_excel_url: string | null;
    ley: string | null;

    numPersonas?:string;
    total?:string;

    estado?: CuentaCobroStatus;
    analista?: CuentaCobroStatus; // approved status
    jefatura?: CuentaCobroStatus; // approved status
}

export interface ApproveCuentaQuery{
    idCuentaCobro:string;
    documento:string;
    tipo_de_usuario:USER_ROLE;
    aprobación:string;
    observación:string|null;
}

export interface CuentaCobroList extends Array<CuentaCobro>{};

export interface Disqualification{
    id:string;
    documentType: string;
    documentNumber: string;
    name:string;
    reason:string;    
    createdAt:string;
}

export interface DisqualificationList extends Array<Disqualification>{};

export interface RevokedUser {   
    Account:string;
    Date:string
    Identification_number:string;
    Full_name:string;
    Reason:string;
    Status:string;
    //@JsonProperty("Documento")
    //@JsonProperty("Nombre")
    //@JsonProperty("Motivo")
    //@JsonProperty("Estado")
}

export interface RevokedUserList extends Array<RevokedUser>{};

export interface RevokedUsersResponse {
  data: RevokedUserList;
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface QueryRectifyRevokedUser {
    accountId:number;
    identificationNumber:number;
}

export interface QueryRectifyListRevokedUser {
    file:File;
    accountId:number;
}

export interface QueryRectifyListRevokedUserResponse {
    reason:string;
}

export interface QueryRectifyRevokedUserResponse{
    identificationNumber: string;
    oldStatus: string;
    newStatus: string;
    oldReason: string;
    //@JsonProperty("Documento")
    //@JsonProperty("Estado_anterior")
    //@JsonProperty("Nuevo_estado")
    //@JsonProperty("Motivo_anterior")
}