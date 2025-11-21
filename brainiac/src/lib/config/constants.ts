export enum CUENTA_COBRO_STATE {
    pendiente="PENDIENTE",
    aprobado= "APROBADO",
    rechazado="RECHAZADO",
}

export const CUENTA_COBRO_STATUS = {
    PENDIENTE: "PENDIENTE",
    APROBADO:  "APROBADO",
    RECHAZADO: "RECHAZADO",
} as const;

export enum USER_ROLE{
    UNDEFINED = "Basico",
    ANALISTA = "ANALISTA",
    JEFATURA = "JEFE",
}

export enum ACCOUNT_PROCESS_ACTION{
    UNDEFINED="UNDEFINED",
    APPROVE="APROBADO",
    REJECT="RECHAZADO"
}
