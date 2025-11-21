import type { IKeycloakRepository } from '@/domain/repositories/keycloak.repository';
import { REPOSITORY_TYPES } from '@/infrastructure/ioc/containers/repositories/repository.types';
import { inject, injectable } from 'inversify';

@injectable()
export default class LogoutKeycloakUseCase {
  private keycloakRepository: IKeycloakRepository;

  constructor(
    @inject(REPOSITORY_TYPES._KeycloakRepository)
    keycloakRepository: IKeycloakRepository,
  ) {
    this.keycloakRepository = keycloakRepository;
  }

  async execute(token?: string): Promise<boolean> {
    if (!token) return false;
    const request = await this.keycloakRepository
      .logout(token)
      .then(() => {
        return true;
      })
      .catch((error) => error);
    if (request instanceof Error) {
      false;
    }

    return request;
  }
}
