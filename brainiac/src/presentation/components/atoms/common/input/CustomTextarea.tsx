import { FC, TextareaHTMLAttributes } from "react";
import { NeutralBlackText } from "../../text";

type CustomInputOneProps = {
  title?: string;
  errors?: React.ReactNode;
  placeholder?: string;
  classNameContainer?: string;
  classNameTextArea?: string;
};

export const CustomTextarea: FC<
  TextareaHTMLAttributes<HTMLTextAreaElement> & CustomInputOneProps
> = (
  props: TextareaHTMLAttributes<HTMLTextAreaElement> & CustomInputOneProps
) => {
  const {
    id,
    name,
    value,
    disabled,
    onChange,
    errors,
    title = "",
    placeholder,
    classNameContainer: ClassName,
    classNameTextArea,
  } = props;

  return (
    <div
      className={` ${
        errors ? " h-[calc(240px)]" : " h-[calc(200px)]"
      } ${ClassName}`}
    >
      <div className="w-full h-full">
        <NeutralBlackText
          className={"text-principal-450 h-[calc(20px)]"}
          text={title}
        />
        <textarea
          id={id}
          name={name}
          disabled={disabled}
          placeholder={placeholder}
          className={`w-full h-[calc(180px)] pl-[calc(15px)] outline-principal-180 border-[calc(1px)] border-principal-400 px-4 py-3 bg-principal-150 text-principal-450 placeholder-principal-450 rounded-[calc(5px)] font-outfit text-[calc(15px)] ${classNameTextArea} ${
            disabled ? "bg-principal-460" : "bg-principal-150"
          }`}
          style={{}}
          value={value}
          onChange={onChange}
        />
        <div className="w-full max-h-10 overflow-y-scroll no-scrollbar">
          {errors}
        </div>
      </div>
    </div>
  );
};
