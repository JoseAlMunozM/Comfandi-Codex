"use client";
import React, { useEffect, useRef } from "react";
import lottie from "lottie-web/build/player/lottie_light";

interface Props {
  className?: string;
}

export const Spinner = ({ className = "" }: Props) => {
  const animationContainer = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (animationContainer.current) {
      const anim = lottie.loadAnimation({
        container: animationContainer.current,
        renderer: "svg",
        loop: true,
        autoplay: true,
        animationData: require("/public/animations/spinner.json"),
      });

      return () => anim.destroy();
    }
  }, []);

  return (
    <div className={`w-24 h-24 ${className}`} ref={animationContainer}></div>
  );
};
