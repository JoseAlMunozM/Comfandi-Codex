"use client";
import React, { useEffect, useRef } from "react";
import lottie from "lottie-web/build/player/lottie_light";

interface Props {
  className?: string;
  primary?: boolean;
}

export const ButtonSpinner = ({ className = "", primary = true }: Props) => {
  const animationContainer = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (animationContainer.current) {
      const anim = lottie.loadAnimation({
        container: animationContainer.current,
        renderer: "svg",
        loop: true,
        autoplay: true,
        animationData: primary
          ? require("/public/animations/principal-button-spinner.json")
          : require("/public/animations/secondary-button-spinner.json"),
      });

      return () => anim.destroy();
    }
  }, []);

  return (
    <div className="flex justify-center items-center opacity-80">
      <div
        className={`w-7 h-7 p-0 mr-1 ${className}`}
        ref={animationContainer}
      />
      <div
        className={`text-start ${
          primary ? "text-principal-150" : "text-principal-700"
        } `}
      >
        <p>Cargando</p>
      </div>
    </div>
  );
};
