"use client";

import Image from "next/image";
import React, { FC, useRef, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { ModalTitle } from "./text/ModalTitle";
import { Spinner } from "../animations";
import { Button } from "../buttons";
import { ModalTitleStatus } from "./text/ModalTitleStatus";
import { CuentaCobro, CuentaCobroStatus } from "@/lib/types/cuentas.types";
import { NeutralBlackText, SpanMessage } from "../../text";
import {
  ACCOUNT_PROCESS_ACTION,
  CUENTA_COBRO_STATUS,
  formatCurrency,
  getStatusColor,
  USER_ROLE,
} from "@/lib";
import { CustomTextarea } from "../input";
import { useFormik } from "formik";
import * as Yup from "yup";

interface Props {
  title: string;
  currentUser: string;
  currentRole: USER_ROLE;
  selectedAccount: CuentaCobro | undefined;
  description: string;
  loading?: boolean;
  containerClass?: string;
  imageClass?: string;
  titleClass?: string;
  imageWidth?: number;
  primaryButtonText?: string;
  SecondaryButtonText?: string;
  onPrimaryClick?: () => void;
  onSecondaryClick?: (reason:string) => void;
  stateVisibleFn: Function;
  stateVisibleVar: boolean;
  processAction: ACCOUNT_PROCESS_ACTION;
  hideSecondaryButton?: boolean;
  lockModal?: boolean;
}

export const ModalProcessAction: FC<Props> = ({
  title,
  currentUser,
  currentRole,
  selectedAccount,
  description,
  loading = false,
  containerClass,
  imageClass,
  titleClass,
  imageWidth = 147,
  primaryButtonText = "Reintentar",
  SecondaryButtonText = "Cancelar",
  onPrimaryClick = () => {},
  onSecondaryClick = () => {},
  stateVisibleFn,
  stateVisibleVar,
  processAction,
  hideSecondaryButton = false,
  lockModal = false,
}) => {
  const [visibleForm, setVisibleForm] = useState(false);
  const [completeForm, setCompleteForm] = useState(false);

  const handlePrimaryClick = () => {
    onPrimaryClick();
    stateVisibleFn(false);
  };

  const handleSecondaryClick = (closeOnly: boolean = false, reason:string = "") => {
    if (!closeOnly) {
      onSecondaryClick(reason);
    }
    stateVisibleFn(false);
  };

  const handleMainClick = () => {
    if (!loading && !lockModal) {
      handleSecondaryClick();
    }
  };

  // Reject reason form

  const reasonFormRef = useRef<HTMLFormElement>(null);

  interface ReasonForm {
    reason:string;
  }

  const initialValue:ReasonForm = {
    reason: "",
  }

  const processSubmit = (reasonForm:ReasonForm) => {
    console.log("Reason Form summit:", reasonForm);
    setCompleteForm(true);
    handleSecondaryClick(false, reasonForm.reason);
  }

  const validation = Yup.object().shape({
    reason: Yup.string()
    .required("Este campo es obligatorio")
    .min(5, "Ingresa por lo menos 5 caracteres")
    .max(2000, "La información supera el limite de 2000 caracteres"),
  });

  const {
    errors,
    handleSubmit,
    handleChange,
    values,
    setValues,
    setFieldValue,
    submitForm,
  } = useFormik({
    initialValues: initialValue,
    onSubmit: processSubmit,
    validationSchema: validation,
  });

  return (
    <>
      {stateVisibleVar && (
        <div
          className={`w-screen h-screen absolute top-0 left-0 z-[90000] flex justify-center items-center bg-principal-800`}
          onClick={() => {
            handleSecondaryClick(true);
          }}
          onKeyDown={() => {}}
          role="button"
        >
          <AnimatePresence>
            <motion.div
              key={"sucursalmodal"}
              initial={{ scale: 0.6, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ type: "spring", damping: 15, stiffness: 200 }}
              className={`relative md:w-[663px] rounded-[20px] flex flex-col items-center shadow-lg bg-principal-150 ${containerClass} subpixel-antialiased`}
              onClick={(e) => e.stopPropagation()}
              onKeyDown={() => {}}
              role="button"
            >
              {!loading && !lockModal && (
                <Image
                  src="/brainiac/static/icons/close-icon-modal.svg"
                  alt="Close icon"
                  width={25}
                  height={25}
                  className={`absolute top-4 right-4 cursor-pointer ${
                    loading ? "hidden" : ""
                  }`}
                  onClick={() => {
                    handleSecondaryClick(true);
                  }}
                  onKeyDown={() => {}}
                  role="button"
                  priority
                />
              )}

              <div className="flex justify-center">
                <Image
                  src="/brainiac/static/icons/bell-icon.svg"
                  alt="download Excel"
                  width={93}
                  height={93}
                />
              </div>

              <div className="w-full block p-5">
                <div className="w-full flex">
                  <div className="flex-none">
                    <ModalTitleStatus
                      text={title}
                      className={`${titleClass}`}
                    />
                  </div>
                  {selectedAccount && (
                    <div className="block flex w-full justify-end">
                      <div className="place-self-center mr-5">
                        <SpanMessage
                          type={getStatusColor(selectedAccount.estado?selectedAccount.estado:CUENTA_COBRO_STATUS.RECHAZADO)}
                          text={selectedAccount.estado?selectedAccount.estado:"Pending"}
                        />
                      </div>
                    </div>
                  )}
                  <div></div>
                </div>
                <p className="justify-left text-md">Usuario {currentRole}</p>
                <p className="justify-left text-xs text-black/40">
                  {currentUser}
                </p>

                {selectedAccount && (
                  <div>
                    <p className="text-end text-xs text-principal-180 font-light">
                      {`Fecha de Creación: ${selectedAccount.creation_date}`}
                    </p>
                    <div className="w-full flex justify-end">
                      {currentRole === USER_ROLE.JEFATURA && (
                        <div className="">
                          <a href={selectedAccount?.document_word_url?selectedAccount?.document_word_url:"#"}>
                            <Image
                              src="/brainiac/static/icons/word.png"
                              alt="download Word"
                              width={40}
                              height={40}
                            />
                          </a>
                        </div>
                      )}

                      {currentRole === USER_ROLE.ANALISTA && (
                        <div className="">
                          <a href={selectedAccount?.document_excel_url?selectedAccount?.document_excel_url:"#"}>
                            <Image
                              src="/brainiac/static/icons/excel.png"
                              alt="download Excel"
                              width={40}
                              height={40}
                            />
                          </a>
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {processAction === ACCOUNT_PROCESS_ACTION.APPROVE && (
                  <div>
                    <hr className="px-5" />
                    <p className="text-[1.5rem] text-[#003DA5] px-20 font-light">
                      ¿Está seguro de aprobar la cuenta de cobro? Esta acción no
                      se puede deshacer y afectará el estado de la cuenta de
                      cobro.
                    </p>
                  </div>
                )}

                {processAction === ACCOUNT_PROCESS_ACTION.REJECT && (
                  <div>
                    <hr className="px-5" />
                    {!visibleForm ? (
                      <p className="text-[1.5rem] text-[#003DA5] px-20 font-light">
                        ¿Está seguro de rechazar la cuenta de cobro? Esta acción
                        no se puede deshacer y afectará el estado de la cuenta
                        de cobro.
                      </p>
                    ) : (
                      <div>
                        <p className="mt-2 text-principal-180 text-md">
                          Observación
                        </p>
                        <NeutralBlackText text=""></NeutralBlackText>
                        <form onSubmit={handleSubmit} ref={reasonFormRef}>
                          <CustomTextarea
                            name="reason"
                            id="reason"
                            title=""
                            placeholder=""
                            value={values.reason}
                            onChange={handleChange}
                            errors={
                              errors.reason ? (
                                <NeutralBlackText
                                  text={errors.reason}
                                  className="text-principal-500"
                                ></NeutralBlackText>
                              ) : null
                            }
                          />
                        </form>
                      </div>
                    )}
                  </div>
                )}
              </div>

              {loading && (
                <div className="pt-8 pb-6">
                  <Spinner />
                </div>
              )}
              {!loading && (
                <div className="flex gap-8 w-[80%] mt-6 pb-8">
                  {processAction === ACCOUNT_PROCESS_ACTION.APPROVE && (
                    <Button
                      label={primaryButtonText}
                      onClick={() => {
                        handlePrimaryClick();
                      }}
                      onKeyDown={() => {}}
                      className="w-full border-principal-700 text-white"
                      primary
                    />
                  )}

                  {processAction === ACCOUNT_PROCESS_ACTION.REJECT && (
                    <Button
                      label={SecondaryButtonText}
                      primary={false}
                      onClick={async () => {
                        if (!completeForm) {
                          setVisibleForm(true);
                          try {
                            await submitForm();
                          } catch (error) {
                            console.log("Error on submit:", error);
                          }
                        } 
                      }}
                      className="w-full bg-principal-520 text-white border-principal-520"
                    />
                  )}
                </div>
              )}
            </motion.div>
          </AnimatePresence>
        </div>
      )}
    </>
  );
};

//export default ModalProcessAction;
