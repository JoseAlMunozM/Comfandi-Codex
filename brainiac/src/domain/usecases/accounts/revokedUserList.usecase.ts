import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { RevokedUsersResponse } from "@/lib";

import { inject, injectable } from "inversify";

@injectable()
export default class RevokedUserListUseCase {
  private accountsRepository: IUAccountServiceRepository;

  constructor(
    @inject(REPOSITORY_TYPES._AccountsRepository)
    accountsRepository: IUAccountServiceRepository
  ) {
    this.accountsRepository = accountsRepository;
  }

  async execute(
    data: {},
    accessToken: string
  ): Promise<RevokedUsersResponse | undefined> {
    return await this.accountsRepository.getRevokedUserList(data, accessToken);
  }
}
