import { CUENTA_COBRO_STATUS } from "../config";

export const getStatusColor = (status: string) => {
  switch (status) {
    case CUENTA_COBRO_STATUS.APROBADO:
      return "success";

    case CUENTA_COBRO_STATUS.PENDIENTE:
      return "warning";

    case CUENTA_COBRO_STATUS.RECHAZADO:
      return "error";

    default:
      return "error";
  }
};

export const formatCurrency = (salary: number) => {
  let formatSalary = Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
  }).format(salary);

  return `${formatSalary.replace(",00", "")}`;
};
