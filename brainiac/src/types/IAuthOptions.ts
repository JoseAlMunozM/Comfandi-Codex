import { JWT } from "next-auth/jwt";
export interface IUserToken extends JWT {
  name: string;
  email: string;
  sub: string;
  access_token: string;
  id_token: string;
  expires_at: number;
  refresh_token: string;
  iat: number;
  exp: number;
  jti: string;
  given_name: string;
  scope: string;
  error: string | undefined;

  acr: string;
  auth_time: number;
  azp: string;
  email_verified: boolean;
  family_name: string;
  identification_number: string;
  identification_type: string;
  iss: string;
  preferred_username: string;
  session_state: string;
  sid: string;
  typ: string;
}
