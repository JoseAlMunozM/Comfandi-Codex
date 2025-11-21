"use client";
import React, { Fragment, useEffect } from "react";
import { signIn, signOut, useSession } from "next-auth/react";
import { ChronuxTemplate } from "@comfanditd/chronux-ui";
import { usePathname } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import lottie from "lottie-web/build/player/lottie_light";
import { appContainer } from "@/infrastructure/ioc/inversify.config";
import LogoutKeycloakUseCase from "@/domain/usecases/keycloak/logoutKeycloak.usecase";
import { USECASES_TYPES } from "@/infrastructure/ioc/containers/usecases/usecases.types";

import { Status } from "../config/constants";
import { LoadingAnimation } from "@/presentation/components/molecules";

interface Props {
  children: React.ReactNode;
}

export const Auth = ({ children }: Props) => {
  const { data: session, status } = useSession();
  const currentPath = usePathname();

  const logout = async () => {
    const logoutUseCase = appContainer.get<LogoutKeycloakUseCase>(
      USECASES_TYPES._LogoutKeycloakUseCase
    );
    await logoutUseCase.execute(session?.access_token).then(() => {
      signOut({ callbackUrl: "/" });
    });
  };

  useEffect(() => {
    const handleAuth = async () => {
      if (status === Status.Loading) return;

      if (status === Status.Unauthenticated || !session) {
        await signIn("keycloak", {
          callbackUrl: `${process.env.NEXT_PUBLIC_SITE_URL}/`,
        });
        return;
      } else if (session && session.error === "RefreshAccessTokenError") {
        await logout();
        return;
      }
    };

    handleAuth();
  }, [session, status]);

  if (status === Status.Loading || status === Status.Unauthenticated) {
    return <LoadingAnimation className="z-[6000]" />;
  }

  /* USE SIDEBAR
  <ChronuxTemplate
    currentPath={currentPath}
    homePage="/brainiac"
    sidebarItems={[]}
    link={Link}
    image={Image}
    onLogout={logout}
    token={session?.access_token}
    lottie={lottie}
  >
    {children}
  </ChronuxTemplate>
  */
 
  return <Fragment>{children}</Fragment>;
};
