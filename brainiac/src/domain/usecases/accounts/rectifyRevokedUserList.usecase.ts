import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { QueryRectifyListRevokedUser, QueryRectifyListRevokedUserResponse } from "@/lib";

import { inject, injectable } from "inversify";

@injectable()
export default class RectifyRevokedUserListUseCase {
  private accountsRepository: IUAccountServiceRepository;

  constructor(
    @inject(REPOSITORY_TYPES._AccountsRepository)
    accountsRepository: IUAccountServiceRepository
  ) {
    this.accountsRepository = accountsRepository;
  }

  async execute(
    data: QueryRectifyListRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyListRevokedUserResponse | undefined> {
    return await this.accountsRepository.rectifyRevokedUserList(data, accessToken);
  }
}
