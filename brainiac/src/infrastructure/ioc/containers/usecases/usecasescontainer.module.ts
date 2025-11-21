import { ContainerModule, interfaces } from 'inversify';
import LogoutKeycloakUseCase from '@/domain/usecases/keycloak/logoutKeycloak.usecase';
import { USECASES_TYPES } from './usecases.types';
import AccountApproveUseCase from '@/domain/usecases/accounts/accountApprove.usecase';
import AccountRejectUseCase from '@/domain/usecases/accounts/accountReject.usecase';
import AccountListUseCase from '@/domain/usecases/accounts/accountList.usecase';
import RevokedUserListUseCase from '@/domain/usecases/accounts/revokedUserList.usecase';
import RectifyRevokedUserUseCase from '@/domain/usecases/accounts/rectifyRevokedUser.usecase';
import DisqualifiedUserListUseCase from '@/domain/usecases/accounts/disqualifiedUserList.usecase';
import RectifyRevokedUserListUseCase from '@/domain/usecases/accounts/rectifyRevokedUserList.usecase';

export const usecasesModule = new ContainerModule((bind: interfaces.Bind) => {
  bind<LogoutKeycloakUseCase>(USECASES_TYPES._LogoutKeycloakUseCase).to(LogoutKeycloakUseCase);

  // Accounts
  bind<AccountApproveUseCase>(USECASES_TYPES._AccountApproveUseCase).to(AccountApproveUseCase);
  bind<AccountRejectUseCase>(USECASES_TYPES._AccountRejectUseCase).to(AccountRejectUseCase);
  bind<AccountListUseCase>(USECASES_TYPES._AccountListUseCase).to(AccountListUseCase);  
  bind<RevokedUserListUseCase>(USECASES_TYPES._RevokedUserListUseCase).to(RevokedUserListUseCase);
  bind<RectifyRevokedUserUseCase>(USECASES_TYPES._RectifyRevokedUserUseCase).to(RectifyRevokedUserUseCase);
  bind<RectifyRevokedUserListUseCase>(USECASES_TYPES._RectifyRevokedUserListUseCase).to(RectifyRevokedUserListUseCase);
  bind<DisqualifiedUserListUseCase>(USECASES_TYPES._DisqualifiedUserListUseCase).to(DisqualifiedUserListUseCase);  
});
