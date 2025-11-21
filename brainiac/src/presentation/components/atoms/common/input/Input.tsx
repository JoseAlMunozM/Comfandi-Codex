
import { InputProps } from "@/types";
import { type FC } from "react";

export const Input: FC<InputProps> = (props) => {
  const { value, type, name, id, placeholder, className } = props;
  return (
    <input
      {...props}
      type={type}
      name={name}
      value={value}
      id={id}
      placeholder={placeholder}
      className={`"px-4 py-2.5 ${className}`}
    ></input>
  );
};
