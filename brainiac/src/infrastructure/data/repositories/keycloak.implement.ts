import { IKeycloakRepository } from '@/domain/repositories/keycloak.repository';
import { NETWORK_TYPES } from '@/infrastructure/ioc/containers/network/network.types';
import { isAxiosError, type AxiosInstance } from 'axios';
import { inject, injectable } from 'inversify';

@injectable()
export default class KeycloakRepositoryImplement implements IKeycloakRepository {
  private axiosInstance: AxiosInstance;

  constructor(@inject(NETWORK_TYPES._AxiosNextInstance) axiosInstance: AxiosInstance) {
    this.axiosInstance = axiosInstance;
  }
  async logout(accessToken: string) {
    try {
      const config = {
        headers: {
          Authorization: 'Bearer ' + accessToken,
        },
      };
      let url = `/api/auth/logout`;

      const resp = await this.axiosInstance.get(url, config);

      if (resp) {
        return true;
      } else {
        return false;
      }
    } catch (error) {
      if (isAxiosError(error)) {
        console.log(error);
      } else {
        throw error;
      }
    }
  }
}
