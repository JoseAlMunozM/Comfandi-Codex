import { AccountOperationResponse } from "@/domain/models";
import { IUAccountServiceRepository } from "@/domain/repositories/accountService.repository";
import { NETWORK_TYPES } from "@/infrastructure/ioc/containers/network/network.types";
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
import type { AxiosInstance } from "axios";
import axios from "axios";
import { inject, injectable } from "inversify";

@injectable()
export default class AccountsRepositoryImplements
  implements IUAccountServiceRepository
{
  private axiosInstance: AxiosInstance;

  constructor(
    @inject(NETWORK_TYPES._AxiosBackEndInstance) axiosInstance: AxiosInstance
  ) {
    this.axiosInstance = axiosInstance;
  }

  async accountApprove(
    data: {},
    accessToken: string
  ): Promise<AccountOperationResponse | undefined> {
    try {
      const config = {
        headers: {
          Authorization: "Bearer " + accessToken,
        },
      };
      const response = await this.axiosInstance
        .post("/api/v1/billing/billing-approval", data, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }
      return;
    } catch (error) {
      console.log(
        `Error al aprobar la cuenta: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async accountReject(
    data: {},
    accessToken: string
  ): Promise<AccountOperationResponse | undefined> {
    try {
      const config = {
        headers: {
          Authorization: "Bearer " + accessToken,
        },
      };
      const response = await this.axiosInstance
        .post("/v1/account/reject/", data, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }
      return;
    } catch (error) {
      console.log(
        `Error al rechazar la cuenta: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async getAccountList(
    data: {},
    accessToken: string
  ): Promise<CuentaCobroList | undefined> {
    try {
      const config = {
        // basic auth
      };
      const response = await this.axiosInstance
        .get("/api/v1/billing/billing-account", config)
        .then((result) => result.data);
      if (response) {
        return response;
      }

      return;
    } catch (error) {
      console.log(
        `Error al rechazar la cuenta: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async getRevokedUserList(
    data: {},
    accessToken: string
  ): Promise<RevokedUsersResponse | undefined> {
    try {
      const config = {
        // basic auth
      };

      let parameters = "";
      if ("page" in data && "size" in data) {
        parameters = `?page=${data.page}&size=${data.size}`;
      }

      if ("accountId" in data) {
        parameters +=
          parameters != ""
            ? `&accountId=${data.accountId}`
            : `?accountId=${data.accountId}`;
      }

      const response = await this.axiosInstance
        .get("/api/v1/courses/users/users-revoked" + parameters, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }

      return;
    } catch (error) {
      console.log(
        `Error al leer la lista de usuarios revocados: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async rectifyRevokedUser(
    data: QueryRectifyRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyRevokedUserResponse | undefined> {
    try {
      const config = {
        // basic auth
      };
      let parameters = `?accountId=${data.accountId}&identificationNumber=${data.identificationNumber}`;

      const response = await this.axiosInstance
        .put("/api/v1/courses/users/correct-user" + parameters, data, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }

      return;
    } catch (error) {
      console.log(
        `Error al corregir el usuario revocados: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async rectifyRevokedUserList(
    data: QueryRectifyListRevokedUser,
    accessToken: string
  ): Promise<QueryRectifyListRevokedUserResponse | undefined> {
    try {
      const config = {
        // basic auth
        headers: {
          "Content-Type": "multipart/form-data",
        },
      };

      const formData = new FormData();
      formData.append("accountId", data.accountId.toString());
      formData.append("file", data.file);

      const response = await this.axiosInstance
        .post("/api/v1/courses/users/update-users-file", formData, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }

      return;
    } catch (error) {
      console.log(
        `Error al corregir el usuario revocados: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }

  async getDisqualifiedUsers(
    data: QueryDisqualifiedUsers,
    accessToken: string
  ): Promise<NotAceptableUserList | undefined> {
    try {
      const config = {
        // basic auth
      };

      let parameters = "";
      if ("startDate" in data && "endDate" in data) {
        parameters = `?startDate=${data.startDate}&endDate=${data.endDate}`;
      }

      const response = await this.axiosInstance
        .get("/api/v1/unavailable-users/find-users" + parameters, config)
        .then((result) => result.data);
      if (response) {
        return response;
      }

      return;
    } catch (error) {
      console.log(
        `Error al leer la lista de usuarios inhabilitados: ${
          axios.isAxiosError(error)
            ? JSON.stringify(error.response?.data || {})
            : error
        }`
      );
      return;
    }
  }
}
