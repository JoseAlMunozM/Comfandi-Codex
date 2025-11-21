"use client";

import Image from "next/image";
import React, { FC, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { ModalTitle } from "./text/ModalTitle";
import { Spinner } from "../animations";
import { Button } from "../buttons";
import { ModalTitleStatus } from "./text/ModalTitleStatus";
import { CuentaCobro, CuentaCobroStatus } from "@/lib/types/cuentas.types";
import { SpanMessage } from "../../text";
import { CUENTA_COBRO_STATUS, formatCurrency, getStatusColor, USER_ROLE } from "@/lib";

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
  onSecondaryClick?: () => void;
  stateVisibleFn: Function;
  stateVisibleVar:boolean;
  hideSecondaryButton?: boolean;
  lockModal?: boolean;
}

// Función que convierte el texto con * * en partes estilizadas
const formatDescription = (text: string) => {
  return text.split(/\n+/).map((line, index) => (
    <span key={"nt-gen-" + index} className="mb-2">
      {line.split(/(\*[^*]+\*)/).map((part, subIndex) =>
        part.startsWith("*") && part.endsWith("*") ? (
          <span key={"ft-gen-" + subIndex} className="font-semibold">
            {part.slice(1, -1)}
          </span>
        ) : (
          part
        )
      )}
    </span>
  ));
};

export const ModalStatus: FC<Props> = ({
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
  hideSecondaryButton = false,
  lockModal = false,
}) => {
  //const [visible, setVisible] = useState(true);

  const handlePrimaryClick = () => {
    onPrimaryClick();
    stateVisibleFn(false);
  };

  const handleSecondaryClick = (closeOnly:boolean = false) => {
    //console.log("Reject modal:", closeOnly);
    if (!closeOnly){
      onSecondaryClick();
    }
    stateVisibleFn(false);
  };

  const handleMainClick = () => {
    if (!loading && !lockModal) {
      handleSecondaryClick(true);
    }
  };

  return (
    <>
      {stateVisibleVar && (
        <div
          className={`w-screen h-screen absolute top-0 left-0 z-[90000] flex justify-center items-center bg-principal-800`}
          onClick={handleMainClick}
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
                  onClick={()=>{handleSecondaryClick(true)}}
                  onKeyDown={() => {}}
                  role="button"
                  priority
                />
              )}

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
                  <p className="text-end text-xs text-principal-180 font-light">
                    {`Fecha de Creación: ${selectedAccount.creation_date}`}
                  </p>
                )}
              </div>

              <div className="w-full px-10 text-center">
                <table className="table-auto w-full text-align-center">
                  <thead>
                    <tr className="border-t-gray-700 text-gray-500">
                      <th>Nro personas</th>
                      <th>Total</th>
                      <th>Ley</th>
                      {currentRole === USER_ROLE.JEFATURA && <th>DOC</th>}
                      {currentRole !== USER_ROLE.UNDEFINED && <th>XML</th>}
                    </tr>
                  </thead>
                  <tbody>
                    <tr className="border-t-2 border-b-2 border-gray-200">
                      <td>{selectedAccount?.numPersonas}</td>
                      <td>
                        {formatCurrency(
                          selectedAccount?.total
                            ? Number(selectedAccount?.total)
                            : 0
                        )}
                      </td>
                      <td>{selectedAccount?.ley}</td>

                      {currentRole === USER_ROLE.JEFATURA && (
                        <td className="">
                          <div className="flex justify-center">
                            <a href={selectedAccount?.document_word_url?selectedAccount?.document_word_url:"#"}>
                              <Image
                                src="/brainiac/static/icons/word.png"
                                alt="download Word"
                                width={40}
                                height={40}
                              />
                            </a>
                          </div>
                        </td>
                      )}

                      {currentRole !== USER_ROLE.UNDEFINED && (
                        <td className="">
                          <div className="flex justify-center">
                            <a href={selectedAccount?.document_excel_url?selectedAccount?.document_excel_url:"#"}>
                              <Image
                                src="/brainiac/static/icons/excel.png"
                                alt="download Excel"
                                width={40}
                                height={40}
                              />
                            </a>
                          </div>
                        </td>
                      )}
                    </tr>
                  </tbody>
                </table>
              </div>
              {loading && (
                <div className="pt-8 pb-6">
                  <Spinner />
                </div>
              )}
              {!loading && (
                <div className="flex gap-8 w-[80%] mt-6 pb-8">
                  <Button
                    label={primaryButtonText}
                    onClick={()=>{handlePrimaryClick()}}
                    className="w-full"
                    primary
                  />
                  {!hideSecondaryButton && (
                    <Button
                      label={SecondaryButtonText}
                      onClick={()=>{handleSecondaryClick()}}
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

//export default ModalStatus;
