import 'next-auth';

declare module 'next-auth' {
  interface Session {
    id_token?: string;
    access_token?: string;
    identification_type?: string;
    identification_number?: string;
    given_name?: string;
    family_name?: string;
    number?: string;
    email?: string;
    edcmfndi?: string;
    error?: string;
    refresh_token?: string;
    name: string;
  }

  interface RefreshToken {
    exp: number;
  }

  interface AccessToken {
    exp: number;
  }
}
