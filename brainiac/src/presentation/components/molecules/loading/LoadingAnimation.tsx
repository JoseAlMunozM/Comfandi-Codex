'use client';
import React, { useEffect, useRef } from 'react';
import lottie from 'lottie-web/build/player/lottie_light';

interface Props {
  className?: string;
  containerClassName?: string;
}

export const LoadingAnimation = ({ className = '', containerClassName }: Props) => {
  const animationContainer = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (animationContainer.current) {
      const anim = lottie.loadAnimation({
        container: animationContainer.current,
        renderer: 'svg',
        loop: true,
        autoplay: true,
        animationData: require('/public/animations/spinner.json'),
      });

      return () => anim.destroy();
    }
  }, []);

  const defaultSizing = 'w-screen h-screen';

  return (
    <div
      className={`bg-principal-680 absolute flex items-center justify-center overflow-hidden ${
        containerClassName ? containerClassName : defaultSizing
      }`}
    >
      <div className={`h-24 w-24 ${className}`} ref={animationContainer}></div>
    </div>
  );
};
