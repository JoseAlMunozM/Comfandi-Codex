import type { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { REPOSITORY_TYPES } from "@/infrastructure/ioc/containers/repositories/repository.types";
import { CuentaCobroList } from "@/lib";
import { inject, injectable } from "inversify";

@injectable()
export default class AccountListUseCase {
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
  ): Promise<CuentaCobroList | undefined> {
    return await this.accountsRepository.getAccountList(data, accessToken);
  }
}
