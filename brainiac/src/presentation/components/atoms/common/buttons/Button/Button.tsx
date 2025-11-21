"use client";
import React from "react";
import { ButtonSpinner } from "../../animations";

interface Props extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  primary?: boolean;
  label?: string;
  primaryClass?: string;
  secondaryClass?: string;
  className?: string;
  onClick?: (...arg0: any) => void;
  type?: "button" | "submit" | "reset";
  isLoading?: boolean;
}

export const Button = ({
  primary = false,
  label = "Sección",
  primaryClass = "bg-principal-700 text-white",
  secondaryClass = " border-[1px] ",
  className = "",
  onClick = () => {},
  type = "button",
  isLoading = false,
  ...rest
}: Props) => {
  return (
    <button
      type={type}
      onClick={onClick}
      className={`rounded-full ${
        isLoading ? "" : "active:scale-[99%]"
      } w-[180px] h-[50px]  ${
        primary
          ? `activeButtonShadow ${primaryClass}`
          : `inactiveButtonShadow ${secondaryClass}`
      } ${className}`}
      {...rest}
      disabled={isLoading}
    >
      {isLoading ? <ButtonSpinner primary={primary} /> : label}
    </button>
  );
};
