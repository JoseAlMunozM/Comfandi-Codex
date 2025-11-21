import {
  CuentaCobroList,
  NotAceptableUserList,
  QueryDisqualifiedUsers,
  QueryRectifyListRevokedUser,
  QueryRectifyListRevokedUserResponse,
  QueryRectifyRevokedUser,
  QueryRectifyRevokedUserResponse,
  RevokedUsersResponse,
} from "@/lib";
import { AccountOperationResponse } from "../models";

export interface IUAccountServiceRepository {
  accountApprove(
    data: {},
    accessToken: string
  ): Promise<AccountOperationResponse | undefined>;

  accountReject(
    data: {},
    accessToken: string
  ): Promise<AccountOperationResponse | undefined>;

  getAccountList(
    data: {},
    accessToken: string
  ): Promise<CuentaCobroList | undefined>;

  getRevokedUserList(
    data: {},
    accessToken: string
  ): Promise<RevokedUsersResponse | undefined>;

  rectifyRevokedUser(
    data: QueryRectifyRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyRevokedUserResponse | undefined>;

  rectifyRevokedUserList(
    data: QueryRectifyListRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyListRevokedUserResponse | undefined>;

  getDisqualifiedUsers(
    data: QueryDisqualifiedUsers,
    accessToken: string
  ): Promise<NotAceptableUserList | undefined>;
}
