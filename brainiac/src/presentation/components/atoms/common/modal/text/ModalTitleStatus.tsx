
import { TextInterface } from "@/types/text/text.types";
import { type FC } from "react";

export const ModalTitleStatus: FC<TextInterface> = ({ text, className = "" }) => {
  return (
    <p
      className={`font-outfit w-full font-semibold mb-2 pt-6 text-principal-180 ${className}`}
    >
      {text}
    </p>
  );
};
