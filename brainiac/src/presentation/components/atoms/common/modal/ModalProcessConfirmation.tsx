"use client";

import Image from "next/image";
import React, { FC, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Button } from "../buttons";

import { Spinner } from "../animations";
import { useFormik } from "formik";


import { CustomTextarea } from "../input";

import { ModalTitleStatus } from "./text/ModalTitleStatus";
import { NeutralBlackText } from "../../text";
import { RevisionStructure, techRevisionModalValidation } from "@/lib";

interface Props {
  title: string;
  message: string;
  loading?: boolean;
  containerClass?: string;
  imageClass?: string;
  titleClass?: string;
  imageWidth?: number;
  primaryButtonText?: string;
  SecondaryButtonText?: string;
  onPrimaryClick?: (reason: string) => void;
  onSecondaryClick?: () => void;
  onClickClose?: () => void;
  hideSecondaryButton?: boolean;
  lockModal?: boolean;
  currentStatus: boolean;
}

export const ModalProcessConfirmation: FC<Props> = ({
  title,
  message,
  loading = false,
  containerClass,
  imageClass,
  titleClass,
  imageWidth = 147,
  primaryButtonText = "Reintentar",
  SecondaryButtonText = "Cancelar",
  onPrimaryClick = () => {},
  onSecondaryClick = () => {},
  onClickClose = () => {},
  hideSecondaryButton = false,
  lockModal = false,
  currentStatus,
}) => {
  const [visible, setVisible] = useState(true);

  const handlePrimaryClick = (message: string) => {
    onPrimaryClick(message);
  };

  const handleSecondaryClick = () => {
    onSecondaryClick();
    onClickClose();
    //setVisible(false);
  };

  const handleMainClick = () => {
    if (!loading && !lockModal) {
      //handleSecondaryClick();
    }
  };

  const initialValues = {
    observation: "",
  };

  const onSubmit = (data: RevisionStructure) => {
    handlePrimaryClick(data.observation);
  };

  const {
    errors,
    handleSubmit,
    handleChange,
    values,
    setValues,
    setFieldValue,
    submitForm,
  } = useFormik({
    initialValues: initialValues,
    onSubmit: onSubmit,
    validationSchema: techRevisionModalValidation,
    validateOnBlur: false,
    validateOnChange: false,
  });

  return (
    <>
      {visible && (
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
                    handleSecondaryClick();
                  }}
                  onKeyDown={() => {}}
                  role="button"
                  priority
                />
              )}

              <div className="flex justify-center pt-5">
                <Image
                  src="/brainiac/static/icons/check-icon-blue.png"
                  alt="Proceso OK"
                  width={125}
                  height={125}
                />
              </div>

              <div className="w-full">
                <ModalTitleStatus text={title} className={`${titleClass}`} />
              </div>

              <div>
                <hr />
                <p className="text-[1.5rem] text-[#003DA5] pt-5 font-light">
                  {message}
                </p>

                {currentStatus == false && (
                  <div className="p-5">
                    <form onSubmit={handleSubmit}>
                      <CustomTextarea
                        name="observation"
                        id="observation"
                        title="Motivos u observaciones por las cuales se rechaza este registro"
                        placeholder=""
                        value={values.observation}
                        onChange={handleChange}
                        errors={
                          errors.observation ? (
                            <NeutralBlackText
                              text={errors.observation}
                              className="text-principal-500"
                            ></NeutralBlackText>
                          ) : null
                        }
                      />
                    </form>
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
                  <Button
                    label={primaryButtonText}
                    onClick={() => {
                      console.log("primary click accept main");

                      if (currentStatus == true) {
                        console.log("primary click accept");
                        handlePrimaryClick("");
                      } else if (
                        currentStatus == false
                      ) {
                        console.log("primary click reject");
                        submitForm();
                      }
                    }}
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
