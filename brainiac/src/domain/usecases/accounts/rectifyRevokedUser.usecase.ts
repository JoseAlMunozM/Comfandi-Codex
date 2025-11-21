import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { QueryRectifyRevokedUser, QueryRectifyRevokedUserResponse } from "@/lib";

import { inject, injectable } from "inversify";

@injectable()
export default class RectifyRevokedUserUseCase {
  private accountsRepository: IUAccountServiceRepository;

  constructor(
    @inject(REPOSITORY_TYPES._AccountsRepository)
    accountsRepository: IUAccountServiceRepository
  ) {
    this.accountsRepository = accountsRepository;
  }

  async execute(
    data: QueryRectifyRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyRevokedUserResponse | undefined> {
    return await this.accountsRepository.rectifyRevokedUser(data, accessToken);
  }
}
