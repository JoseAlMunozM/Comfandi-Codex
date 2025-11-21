import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { NotAceptableUserList, QueryDisqualifiedUsers } from "@/lib";

import { inject, injectable } from "inversify";

@injectable()
export default class DisqualifiedUserListUseCase {
  private accountsRepository: IUAccountServiceRepository;

  constructor(
    @inject(REPOSITORY_TYPES._AccountsRepository)
    accountsRepository: IUAccountServiceRepository
  ) {
    this.accountsRepository = accountsRepository;
  }

  async execute(
    data: QueryDisqualifiedUsers,
    accessToken: string
  ): Promise<NotAceptableUserList | undefined> {
    return await this.accountsRepository.getDisqualifiedUsers(
      data,
      accessToken
    );
  }
}
