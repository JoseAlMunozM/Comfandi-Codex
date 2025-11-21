"use client"

import { ReactNode } from "react";

interface Props {
    className?: string;
    type: "success"|"warning"|"error";
    text?: string|ReactNode;
}

export const SpanMessage = ({type="success", text=''}:Props) =>{

    const successClass = "bg-green-100 text-green-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-sm dark:bg-green-900 dark:text-green-300"
    const warningClass = "bg-yellow-100 text-yellow-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-sm dark:bg-yellow-900 dark:text-yellow-300";
    const errorClass = "bg-red-100 text-red-800 text-sm font-medium me-2 px-2.5 py-0.5 rounded-sm dark:bg-red-900 dark:text-red-300"
    
    let style="";

    switch (type) {
        case "success":
            style=successClass;        
            break;
        case "warning":
            style=warningClass;        
            break;        
        case "error":
            style=errorClass;        
            break;
    
        default:
            style=successClass;
            break;
    }

    return(
        <span className={"rounded-md "+style}>
            {text}
        </span>
    );
};