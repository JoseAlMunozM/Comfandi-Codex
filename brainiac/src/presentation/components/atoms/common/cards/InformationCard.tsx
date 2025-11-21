import { MdMoveToInbox, MdOutbox } from "react-icons/md";
import { TfiLayoutMenuSeparated } from "react-icons/tfi";
import { FaArrowTrendUp, FaArrowTrendDown } from "react-icons/fa6";


import { CUENTA_COBRO_STATE } from "@/lib";
import { FC } from "react";

interface infoCardProps{
    accountStatus: CUENTA_COBRO_STATE;
    percentage: number;
    processed: number;
    onClickFn: Function;   
}

export const InformationCard:FC<infoCardProps> = ({accountStatus, percentage, processed, onClickFn}) => {

  const spanColorSuccessClass = "bg-green-100 text-green-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-md";
  const textColorSuccessClass = "text-green-800";

  const spanColorErrorClass = "bg-red-100 text-red-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-md";
  const textColorErrorClass = "text-red-800";

  const spanColorWarnClass = "bg-yellow-100 text-yellow-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-md";
  const textColorWarnClass = "text-yellow-800";

  const getTitle = () => {
    switch (accountStatus) {
        case CUENTA_COBRO_STATE.aprobado:
            return "Procesada";
        case CUENTA_COBRO_STATE.rechazado:
            return "Rechazada"
        case CUENTA_COBRO_STATE.pendiente:
            return "Pendiente"            
        default:
            return ""
    }
  }

  const getStyleText = () => {
    switch (accountStatus) {
        case CUENTA_COBRO_STATE.aprobado:
            return textColorSuccessClass;
        case CUENTA_COBRO_STATE.rechazado:
            return textColorErrorClass;
        case CUENTA_COBRO_STATE.pendiente:
            return textColorWarnClass;            
        default:
            return ""
    }
  }

  const getSpanStyle = () => {
    switch (accountStatus) {
        case CUENTA_COBRO_STATE.aprobado:
            return spanColorSuccessClass;
        case CUENTA_COBRO_STATE.rechazado:
            return spanColorErrorClass;
        case CUENTA_COBRO_STATE.pendiente:
            return spanColorWarnClass;            
        default:
            return ""
    }
  }

  const getPercentageIcon = () => {
    switch (accountStatus) {
        case CUENTA_COBRO_STATE.aprobado:
            return <FaArrowTrendUp
                className={getStyleText() + " text-[1.5rem]"}
            />;
        case CUENTA_COBRO_STATE.rechazado:
            return <FaArrowTrendDown 
                className={getStyleText() + " text-[1.5rem]"}
            />;
        case CUENTA_COBRO_STATE.pendiente:
            return <FaArrowTrendUp
                className={getStyleText() + " text-[1.5rem]"}
            />;
        default:
            return ""
    }
  }

  const getTitleIcon = () => {
    switch (accountStatus) {
        case CUENTA_COBRO_STATE.aprobado:
            return <MdMoveToInbox
                className={getStyleText() + " text-[1.5rem]"}
            />;
        case CUENTA_COBRO_STATE.rechazado:
            return <MdOutbox 
                className={getStyleText() + " text-[1.5rem]"}
            />;
        case CUENTA_COBRO_STATE.pendiente:
            return <MdMoveToInbox
                className={getStyleText() + " text-[1.5rem]"}
            />;
        default:
            return ""
    }
  }  
  
  return (
    <div className="w-full bg-white rounded-lg p-7 shadow-md">
      <div className="w-full flex">
        <div className="w-full">
        <p className="font-light mx-3 text-[1.2rem] flex">
          {getTitleIcon()}{" "}
          <span className="ml-2 mt-[-3px]">{`Cuentas de Cobro ${getTitle()}s`}</span>
        </p>
        </div>
        <div className="">
          <div className="flex justify-end">
            <p className="">
              <TfiLayoutMenuSeparated />
            </p>
          </div>
        </div>
      </div>

      <div className="w-full flex">
        <div>
          <span className="font-bold text-[4rem]">{processed}</span>
        </div>
        <div className="w-full block">
          <div className="flex justify-end mr-5 mt-10">
            <p className="mx-3 flex">
              {" "}
              {getPercentageIcon()}
              {" "}
              <span className={getStyleText() + " text-sm ml-2 "}>
                {percentage}% desde el último mes
              </span>
            </p>
          </div>
        </div>
      </div>
      <div>
        <p className="text-gray-600">{`Última cuenta ${getTitle().toLowerCase()}`}</p>
      </div>

      <div className="flex justify-end mr-5 mt-7">
        <div className="hover:cursor-pointer" onClick={()=>{onClickFn()}} onKeyDown={()=>{}}>
            <span className={getSpanStyle()}>{`Ver cuentas ${getTitle().toLowerCase()}s`}</span>
        </div>
      </div>
    </div>
  );
};
