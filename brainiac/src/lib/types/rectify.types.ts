export interface QueryUpdateUsers {
  file: File; // MultipartFile
  accountId: number;
}

export enum RECTIFY_ACCOUNT_STATUS {
  DEBITED = "COBRADO",
  PENDING = "NO COBRADO",
  REVIEW = "POR REVISAR",
}

export enum RECTIFY_IDENTIFICATION_TYPES {
  NIT = "NIT",
  SIE = "SIE",
  NUIP = "NUIP",
  SC = "SC",
  CC = "CC",
  TI = "TI",
  PA = "PA",
  CE = "CE",
  RC = "RC",
  PEP = "PEP",
  CD = "CD",
  TE = "TE",
  PPT = "PPT",
}

export interface CsvColumns {
  accountId: string;
  document: string;
  documentType: RECTIFY_IDENTIFICATION_TYPES;
  status: RECTIFY_ACCOUNT_STATUS;
  reason: string;
}

export interface CsvProcessResult {
  reason: string;
}

export interface QueryRectifySingleUser {
  accountId: string;
  identificationNumber: string;
}
