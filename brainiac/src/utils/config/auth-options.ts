import { Account, AuthOptions, Session } from "next-auth";
import KeycloakProvider from "next-auth/providers/keycloak";
import { JWT } from "next-auth/jwt";
import { IUserToken } from "@/types";

const clientId = process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID ?? "";
const realm = process.env.NEXT_PUBLIC_KEYCLOAK_ISSUER ?? "";

export async function refreshAccessToken(token: IUserToken) {
  const resp = await fetch(
    `${process.env.NEXT_PUBLIC_KEYCLOAK_REFRESH_TOKEN}`,
    {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },

      body: new URLSearchParams({
        client_id: clientId,
        client_secret: "",
        grant_type: "refresh_token",
        refresh_token: token.refresh_token ?? "",
      }),
      method: "POST",
    }
  );

  const refreshToken = await resp.json();

  if (!resp.ok) throw refreshToken;

  return {
    ...token,
    access_token: refreshToken.access_token,
    id_token: refreshToken.id_token,
    expires_at: Math.floor(Date.now() / 1000) + refreshToken.expires_in,
    refresh_token: refreshToken.refresh_token,
  };
}

export const authOptions: AuthOptions = {
  providers: [
    KeycloakProvider({
      clientId,
      clientSecret: "",
      issuer: realm,
    }),
  ],
  pages: {
    signIn: "/brainiac/api/auth/signin/keycloak",
    error: "/",
  },
  callbacks: {
    async jwt({ token, account }: { token: JWT; account: Account | null }) {
      const nowTimeStamp = Math.floor(Date.now() / 1000);
      let userToken = token as IUserToken;
      if (
        account &&
        account.access_token &&
        account.id_token &&
        account.expires_at &&
        account.refresh_token
      ) {
        // account is only available the first time this callback is called on a new session (after the user signs in)
        userToken.access_token = account.access_token;
        userToken.id_token = account.id_token;
        userToken.expires_at = account.expires_at;
        userToken.refresh_token = account.refresh_token;
        return token;
      } else if (nowTimeStamp < userToken.expires_at) {
        // token has not expired yet, return it
        return token;
      } else {
        // token is expired, try to refresh it
        console.log("Token has expired. Will refresh...");
        try {
          const refreshedToken = await refreshAccessToken(userToken);
          console.log("Token is refreshed.");
          return refreshedToken;
        } catch (error) {
          console.error("Error refreshing access token", error);

          return { ...token, error: "RefreshAccessTokenError" };
        }
      }
    },

    async session({ session, token }: { session: Session; token: JWT }) {
      let userToken = token as IUserToken;
      session.access_token = userToken.access_token;
      session.id_token = userToken.id_token;
      session.refresh_token = userToken.refresh_token;
      session.error = userToken.error;

      return session;
    },
  },
};
