"use client";
import React from "react";
import { Paragraph, SecondaryTitle } from "@comfanditd/chronux-ui";
import { usePathname } from "next/navigation";
import Link from "next/link";

export default function NotFound() {
  const currentPath = usePathname();

  return (
        <div>
        <Paragraph
          text={`La página que solicitaste ${currentPath}, no existe.`}
          className="mb-8"
        />
        <Link href="/">
          <button>Ir al inicio</button>
        </Link>
      </div>
    
  );
}
