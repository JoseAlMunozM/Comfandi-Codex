export interface IKeycloakRepository {
  logout(accessToken: string): Promise<boolean | undefined>;
}
