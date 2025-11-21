import { ContainerModule, interfaces } from 'inversify';
import { REPOSITORY_TYPES } from './repository.types';
import { IKeycloakRepository } from '@/domain/repositories/keycloak.repository';
import KeycloakRepositoryImplement from '@/infrastructure/data/repositories/keycloak.implement';
import { IUAccountServiceRepository } from '@/domain/repositories/accountService.repository';
import AccountsRepositoryImplements from '@/infrastructure/data/repositories/account.implements';

export const repositoryModule = new ContainerModule((bind: interfaces.Bind) => {
  bind<IKeycloakRepository>(REPOSITORY_TYPES._KeycloakRepository).to(KeycloakRepositoryImplement);
  bind<IUAccountServiceRepository>(REPOSITORY_TYPES._AccountsRepository).to(AccountsRepositoryImplements);
});
