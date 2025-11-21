export interface QueryDisqualifiedUsers {
  startDate: string;
  endDate: string;
}

export interface NotAceptableUser {
  Identificacion: string;
  Tipo_identificacion: string;
  Nombre_completo: string;
  Celular: string;
  Fec_inicio: string;
  Fec_inhabilitacion: string;
  Email: string;
  Avance: string;
  Causa: string;
}

export interface NotAceptableUserList extends Array<NotAceptableUser> {}

export interface NotAceptableUsersData {
  users: NotAceptableUserList;
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface NotAceptableUsersResponse {
  data: NotAceptableUsersData;
}
