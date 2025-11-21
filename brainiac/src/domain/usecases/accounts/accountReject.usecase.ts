import { AccountOperationResponse } from "@/domain/models";
import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { inject, injectable } from "inversify";

@injectable()
export default class AccountRejectUseCase {
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
  ): Promise<AccountOperationResponse | undefined> {
    return await this.accountsRepository.accountReject(data, accessToken);
  }
}
