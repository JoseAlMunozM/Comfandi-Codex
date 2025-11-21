import * as Yup from "yup";

export const techRevisionModalValidation = Yup.object().shape({
  observation: Yup.string()
    .min(5, "Ingresa por lo menos 5 caracteres")
    .max(2000, "La descripción ingresada es muy extensa.")
    .required(
      "Por favor ingresa el motivo por el cual se rechaza este registro."
    ),
});
