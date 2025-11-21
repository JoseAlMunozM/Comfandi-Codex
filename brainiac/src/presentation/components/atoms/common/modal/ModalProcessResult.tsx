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
import {
  ACCOUNT_PROCESS_ACTION,
  formatCurrency,
  getStatusColor,
  USER_ROLE,
} from "@/lib";

interface Props {
  title: string;
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
  stateVisibleVar: boolean;
  processAction: ACCOUNT_PROCESS_ACTION;
  hideSecondaryButton?: boolean;
  lockModal?: boolean;
}

export const ModalProcessResult: FC<Props> = ({
  title,
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
  //const [visible, setVisible] = useState(true);

  const handlePrimaryClick = () => {
    onPrimaryClick();
    stateVisibleFn(false);
  };

  const handleSecondaryClick = (closeOnly: boolean = false) => {
    if (!closeOnly) {
      onSecondaryClick();
    }
    stateVisibleFn(false);
  };

  const handleMainClick = () => {
    if (!loading && !lockModal) {
      handleSecondaryClick();
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
                  src="/brainiac/static/icons/check-icon.svg"
                  alt="download Excel"
                  width={93}
                  height={93}
                />
              </div>

              <div className="w-full p-5">
                <ModalTitleStatus text={title} className={`${titleClass}`} />
              </div>

              <div>
                <hr className="px-5" />
                <p className="text-[1.5rem] text-[#003DA5] px-20 font-light">
                  Tu solicitud ha sido procesada con éxito. En breve, recibirás
                  un correo de confirmación con todos los detalles de la
                  operación.
                </p>
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
                    onClick={handlePrimaryClick}
                    onKeyDown={() => {}}
                    className="w-full"
                    primary
                  />
                </div>
              )}
            </motion.div>
          </AnimatePresence>
        </div>
      )}
    </>
  );
};

//export default ModalProcessResult;
