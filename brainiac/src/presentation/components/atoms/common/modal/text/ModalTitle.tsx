import { TextInterface } from "@/types/text/text.types";
import { type FC } from "react";

export const ModalTitle: FC<TextInterface> = ({ text, className = "" }) => {
  return (
    <h4
      className={`font-outfit text-[1rem] text-center w-[60%] font-semibold mb-2 pt-6 text-principal-180 ${className}`}
    >
      {text}
    </h4>
  );
};
