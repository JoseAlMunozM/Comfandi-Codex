"use-client";
import {
  type ReactElement,
  useState,
  useEffect,
  type FC,
  type ChangeEvent,
  InputHTMLAttributes,
  ForwardedRef,
} from "react";
import React from "react";

import { TextSearchInputProps } from "@/types";
import { Input } from "./Input";
import { NeutralBlackText } from "../../text";

export const TextSearchInput: React.ForwardRefRenderFunction<
  HTMLInputElement,
  TextSearchInputProps & InputHTMLAttributes<HTMLInputElement>
> = (
  {
    id,
    name,
    style,
    dataType,
    step,
    onlyNumbers,
    placeholder = "",
    label = false,
    hasIcon,
    onChange,
    disabledButFocusable,
    disabled,
    className,
    value,
    classNameText,
    classNameLabel,
    basePath,
    ...props
  },
  ref: ForwardedRef<HTMLInputElement>
) => {

  const searchInput = (
    <Input
          style={style}
          pattern={onlyNumbers ? "^$d{1,3}(,d{3})*(.d+)?$" : ".*"}
          disabled={disabled}
          datatype={dataType}
          id={id}
          readOnly={disabledButFocusable}
          // style={disabledButFocusable ? { pointerEvents: "none" } : {}}
          value={value}
          onChange={onChange}
          name={name ?? ""}
          type={"text"}
          step={step}
          placeholder={placeholder}
          className={`h-[calc(48px)] ${
            hasIcon ? "pl-[calc(35px)]" : "pl-[calc(15px)]"
          } placeholder-principal-190 outline-principal-180 w-full ${
            disabled ? "bg-principal-460" : "bg-principal-150"
          } ${
            classNameText ?? "text-principal-180"
          } font-outfit text-[calc(15px)]`}
          {...props}
        />
  )
  
  return (
    <div className={`w-full ${className}`}>
      {label && (
        <NeutralBlackText className={classNameLabel} text={placeholder} />
      )}

      {hasIcon && (
        <label className="relative block">
        <span className="absolute inset-y-0 left-0 flex items-center pl-3">
          <svg
            className="h-5 w-5 fill-black"
            xmlns="http://www.w3.org/2000/svg"
            x="0px"
            y="0px"
            width="30"
            height="30"
            viewBox="0 0 30 30"
          >
            <path d="M 13 3 C 7.4889971 3 3 7.4889971 3 13 C 3 18.511003 7.4889971 23 13 23 C 15.396508 23 17.597385 22.148986 19.322266 20.736328 L 25.292969 26.707031 A 1.0001 1.0001 0 1 0 26.707031 25.292969 L 20.736328 19.322266 C 22.148986 17.597385 23 15.396508 23 13 C 23 7.4889971 18.511003 3 13 3 z M 13 5 C 17.430123 5 21 8.5698774 21 13 C 21 17.430123 17.430123 21 13 21 C 8.5698774 21 5 17.430123 5 13 C 5 8.5698774 8.5698774 5 13 5 z"></path>
          </svg>
        </span>
          {searchInput}
        </label>)
        }

        {
          !hasIcon && (searchInput)
        }
    </div>
  );
};
