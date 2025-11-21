import { InputHTMLAttributes } from "react";
import { SelectOption } from "./select.types";

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  type?: string;
  name: string;
  id?: string;
  placeholder: string;
  prefixImage?: string;
  className?: string;
}

export interface TextSearchInputProps {
    id?: string;
    dataType?: string;
    decimal?: number;
    defaultText?: string;
    step?: string | number;
    onlyNumbers?: boolean;
    type?: string;
    name?: string;
    disabled?: boolean;
    disabledButFocusable?: boolean;
    value?: string;
    placeholder?: string;
    label?: boolean;
    hasIcon?: boolean;
    icon?: React.ReactNode;
    className?: string;
    classNameText?: string;
    classNameLabel?: string;
    classNameSelect?: string;
    basePath?: boolean;
    onChange?: (data: any) => void;
    defaultOption?: string;
    options?: SelectOption[];
    inError?: boolean;
    errorMsg?: string;
  }