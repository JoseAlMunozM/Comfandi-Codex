"use client";

import {
  ACCOUNT_PROCESS_ACTION,
  CUENTA_COBRO_STATE,
  CUENTA_COBRO_STATUS,
  getStatusColor,
  USER_ROLE,
} from "@/lib";
import {
  InformationCard,
  ModalProcessAction,
  ModalProcessResult,
  ModalStatus,
  SpanMessage,
} from "@/presentation";
import { TextSearchInput } from "@/presentation/components/atoms/common/input/TextSearchInput";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { IoMdOpen } from "react-icons/io";
import { RxCounterClockwiseClock } from "react-icons/rx";
import { CgBandAid } from "react-icons/cg";



import {
  ApproveCuentaQuery,
  CuentaCobro,
  CuentaCobroList,
  TIPO_CUENTA_COBRO,
} from "@/lib/types/cuentas.types";
import { useSession } from "next-auth/react";
import { jwtDecode } from "jwt-decode";
import { IUserToken } from "@/types";
import { LoggedUserInfo } from "@/types/status";

import AccountApproveUseCase from "@/domain/usecases/accounts/accountApprove.usecase";
import { appContainer } from "@/infrastructure";
import { USECASES_TYPES } from "@/infrastructure/ioc/containers/usecases/usecases.types";
import AccountRejectUseCase from "@/domain/usecases/accounts/accountReject.usecase";
import Link from "next/link";
import AccountListUseCase from "@/domain/usecases/accounts/accountList.usecase";
import { useRouter } from "next/navigation";

export const HomePageTemplate = () => {
  const [accountArray, setAccountsArray] = useState<CuentaCobroList>();
  const [accountsCompleteArray, setAccountsCompleteArray] =
    useState<CuentaCobroList>();
  const [activeFilter, setActiveFilter] = useState<CUENTA_COBRO_STATE | null>(
    null
  );

  const [clickAccount, setClickAccount] = useState<CuentaCobro>();
  const [showStatusModal, setShowStatusModal] = useState(false);

  const [showProcessModal, setShowProcessModal] = useState(false);
  const [processAction, setProcessAction] = useState<ACCOUNT_PROCESS_ACTION>(
    ACCOUNT_PROCESS_ACTION.UNDEFINED
  );

  const [showResultModal, setShowResultModal] = useState(false);

  const [userRole, setUserRole] = useState<LoggedUserInfo>({
    name: "Usuario",
    role: USER_ROLE.UNDEFINED,
  });

  const [menuIsOpen, setMenuIsOpen] = useState(false);

  const openMenu = () => {
    setMenuIsOpen(!menuIsOpen);
  };

  const { data: session } = useSession();
  const router = useRouter();

  // Search input manage
  const searchFormRef = useRef<HTMLFormElement>(null);

  const onChangeSearch = (value: string) => {
    console.log("search string:", value);
  };

  const onClickEdit = (cuenta: CuentaCobro) => {
    setClickAccount(cuenta);
    setShowStatusModal(true);
  };

  const onClickRectify = (accountId: string) =>{
    router.push("/rectify/"+accountId);
  }

  const loadAccountArray = async () => {
    const queryData = {};
    const token = session?.access_token ? session?.access_token : "";
    const queryAccountList = appContainer.get<AccountListUseCase>(
      USECASES_TYPES._AccountListUseCase
    );

    let response = await queryAccountList.execute(queryData, token);
    console.log("List Process result:", response);

    // Use mock data if no backend
    if (!response) {
      console.error("Cant read the accounts from server, result:", response);
      return;
    }

    // add account to work
    let added = {
      billingAccountId: "999",
      billing_account_type: TIPO_CUENTA_COBRO.EMPLOYABILITY,
      billing_account_name: "Test account",
      creation_date: "2025-04-15T01:52:00.543166",
      document_word_url: null,
      document_excel_url: null,
      ley: "LEY 2069",

      numPersonas: "35",
      total:"1000000",

      estado: CUENTA_COBRO_STATUS.RECHAZADO,
      analista: CUENTA_COBRO_STATUS.PENDIENTE,
      jefatura: CUENTA_COBRO_STATUS.RECHAZADO,
    } as CuentaCobro;
    let custom = [added, ...response];

    // used in UI (Filter)
    setAccountsArray(custom);
    // complete list, not modify
    setAccountsCompleteArray(custom);
  };

  const getUserRole = () => {
    let userInfo: IUserToken = jwtDecode(session?.access_token!);

    if ("scope" in userInfo) {
      const scopeList = userInfo.scope.split(" ");

      // EXAMPLE SEARCH ON JWT
      if (scopeList.find((e) => e === "identification")) {
        console.log("Analista ROL");
        //setUserRole({ name: userInfo.given_name, role: USER_ROLE.ANALISTA });
      }

      if (scopeList.find((e) => e === "digital_identity")) {
        console.log("Jefatura ROL");
        setUserRole({ name: userInfo.given_name, role: USER_ROLE.JEFATURA });
      }
    }
    //console.log(userInfo);
  };

  const processAcceptQuery = (cuenta: CuentaCobro) => {
    //console.log("Accept action")

    setShowProcessModal(true);
    setProcessAction(ACCOUNT_PROCESS_ACTION.APPROVE);
  };

  const processRejectQuery = (cuenta: CuentaCobro) => {
    //console.log("Reject action")

    setShowProcessModal(true);
    setProcessAction(ACCOUNT_PROCESS_ACTION.REJECT);
  };

  const confirmAcceptQuery = async (cuenta: CuentaCobro) => {
    console.log("Confirm Accept");
    const token = session?.access_token ? session?.access_token : "";
    const userInfo: IUserToken = jwtDecode(token);

    const queryData: ApproveCuentaQuery = {
      idCuentaCobro: cuenta.billingAccountId,
      aprobación: ACCOUNT_PROCESS_ACTION.APPROVE, // "APROBADO"
      tipo_de_usuario: userRole.role,
      documento: userInfo.identification_number,
      observación: null,
    };

    const queryAccountApprove = appContainer.get<AccountApproveUseCase>(
      USECASES_TYPES._AccountApproveUseCase
    );
    const response = await queryAccountApprove.execute(queryData, token);
    console.log("Approve Process result:", response);

    if (!response) {
      console.error("error al procesar desde el servidor");
      return [];
    }

    const queryServerConfirm = true;
    if (queryServerConfirm) {
      setShowResultModal(true);
    } else {
      console.log("Error on confirm account");
    }
    setProcessAction(ACCOUNT_PROCESS_ACTION.UNDEFINED);
    setClickAccount(undefined);
  };

  const confirmRejectQuery = async (
    cuenta: CuentaCobro,
    rejectReason: string
  ) => {
    //console.log("Confirm Reject")
    console.log("Reason received:", rejectReason);

    const token = session?.access_token ? session?.access_token : "";
    const userInfo: IUserToken = jwtDecode(token);

    const queryData: ApproveCuentaQuery = {
      idCuentaCobro: cuenta.billingAccountId,
      aprobación: ACCOUNT_PROCESS_ACTION.REJECT, // "APROBADO"
      tipo_de_usuario: userRole.role,
      documento: userInfo.identification_number,
      observación: rejectReason,
    };
    /*
    const queryAccountApprove = appContainer.get<AccountRejectUseCase>(
      USECASES_TYPES._AccountRejectUseCase
    );
    const response = await queryAccountApprove.execute(queryData, token);
    */
    const queryAccountApprove = appContainer.get<AccountApproveUseCase>(
      USECASES_TYPES._AccountApproveUseCase
    );
    const response = await queryAccountApprove.execute(queryData, token);
    console.log("Reject Process result:", response);

    const queryServerReject = true;
    if (queryServerReject) {
      setShowResultModal(true);
    } else {
      console.log("Error on confirm account");
    }
    setProcessAction(ACCOUNT_PROCESS_ACTION.UNDEFINED);
    setClickAccount(undefined);
  };

  const filterAccountsList = (stateFilter: CUENTA_COBRO_STATE) => {
    if (activeFilter && activeFilter == stateFilter) {
      setActiveFilter(null);
      setAccountsArray(accountsCompleteArray);
    } else {
      setActiveFilter(stateFilter);
      const filteredInfo = accountsCompleteArray?.filter((acc) => {
        if (acc.estado == stateFilter) {
          return acc;
        }
      });
      //console.log("filtered:", filteredInfo);
      if (filteredInfo) {
        setAccountsArray(filteredInfo);
      }
    }
  };

  useEffect(() => {
    loadAccountArray();
    getUserRole();
  }, []);

  return (
    <div className="block w-full h-full overflow-y-hidden">
      <div className="h-screen overflow-y-auto pb-10">
        <div className="bg-[#003DA5] w-full h-80 block pt-5 px-3 bg-[url(/brainiac/static/image/header-bg.svg)] bg-right bg-no-repeat">
          <div className="block px-[40px]">
            <div className="w-full flex">
              <div className="">
                <Image
                  src="/brainiac/static/image/comfandi_light.png"
                  alt="Comfandi"
                  width={150}
                  height={75}
                />
              </div>

              <div
                className="flex justify-end w-full mr-5 mt-10"
                onClick={(e) => {
                  openMenu();
                }}
                onKeyUp={() => {}}
              >
                <div className="">
                  <Image
                    src="/brainiac/static/icons/user-icon.png"
                    alt="Avatar"
                    width={50}
                    height={50}
                  />
                </div>

                <div className="mx-2">
                  <p className="text-white text-lg">{userRole.name}</p>
                  <p className="text-white/60 text-md mt-[-5px]">
                    Usuario {userRole.role}
                  </p>
                </div>

                <div className="pt-3">
                  {menuIsOpen ? (
                    <div
                      className="dropdown dropdown-start origin-top-right absolute right-[-50px]"
                      role="menu"
                    >
                      <label
                        tabIndex={0}
                        className="btn btn-ghost btn-circle avatar"
                      ></label>
                      <ul
                        tabIndex={0}
                        className="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52"
                      >
                        <li className="">
                          <Link
                            href={"/disqualification"}
                            className="block px-4 py-2 text-sm text-gray-700 data-[focus]:bg-gray-100 data-[focus]:text-gray-900 data-[focus]:outline-none"
                          >
                            Inhabilitaciones
                          </Link>
                        </li>
                        <Link
                          href={"/api/auth/logout"}
                          className="block px-4 py-2 text-sm text-gray-700 data-[focus]:bg-gray-100 data-[focus]:text-gray-900 data-[focus]:outline-none"
                        >
                          Cerrar sesión
                        </Link>
                      </ul>
                    </div>
                  ) : (
                    ""
                  )}
                  <Image
                    src="/brainiac/static/icons/arrow-down.png"
                    alt="Comfandi"
                    width={24}
                    height={24}
                  />
                </div>
              </div>
            </div>

            <p className="mt-10 text-[2rem] text-white">
              Hola <span className="font-bold">{userRole.name}!</span>👋
            </p>

            <p className="mt-2 text-[#05C3DD] text-lg">
              Proceso cuenta de cobro
            </p>
          </div>
        </div>

        <div className="block px-[40px] mt-[-60px]">
          <div className="flex flex-col md:flex-row gap-4">
            <InformationCard
              key={"iCardA"}
              onClickFn={() => {
                filterAccountsList(CUENTA_COBRO_STATE.aprobado);
              }}
              accountStatus={CUENTA_COBRO_STATE.aprobado}
              percentage={5}
              processed={1567}
            />
            <InformationCard
              key={"iCardR"}
              onClickFn={() => {
                filterAccountsList(CUENTA_COBRO_STATE.rechazado);
              }}
              accountStatus={CUENTA_COBRO_STATE.rechazado}
              percentage={2}
              processed={123}
            />
            <InformationCard
              key={"iCardP"}
              onClickFn={() => {
                filterAccountsList(CUENTA_COBRO_STATE.pendiente);
              }}
              accountStatus={CUENTA_COBRO_STATE.pendiente}
              percentage={2}
              processed={820}
            />
          </div>
        </div>

        <div className="block px-[40px] mt-5">
          <div className="rounded-lg shadow-md border-[1px] bg-white px-10">
            <div className="block md:flex my-10">
              <p className="font-bold mx-3 text-[2rem] flex">
                <RxCounterClockwiseClock />{" "}
                <span className="mx-2 mt-[-8px] w-[500px]">
                  Registro de cuentas de Cobro
                </span>
              </p>

              <div className="flex justify-end w-full mr-5">
                <form onSubmit={() => {}} ref={searchFormRef}>
                  <TextSearchInput
                    id={"inputSearchJob"}
                    name={"searchString"}
                    label={false}
                    hasIcon={true}
                    onChange={(event) => {
                      onChangeSearch(event.target.value);
                    }}
                    placeholder={"Buscar"}
                    classNameText="w-full text-principal-450 placeholder-principal-450 border-[calc(1px)] rounded-r-[calc(5px)] bg-principal-460"
                    disabled={false}
                    value={""}
                  />
                </form>
              </div>
            </div>

            <div className="block text-center overflow-y-auto">
              {/**border-2 border-dotted border-[#000] */}

              <table className="table-auto w-full text-align-center">
                <thead>
                  <tr className="border-t-gray-700 text-gray-500">
                    <th>Tipo de cuenta</th>
                    <th>Ley</th>
                    <th>Acción</th>
                    <th>Estado</th>
                    <th>Analista</th>
                    <th>Jefatura</th>
                    {userRole.role === USER_ROLE.JEFATURA && <th>DOC</th>}
                    {userRole.role !== USER_ROLE.UNDEFINED && <th>XML</th>}
                  </tr>
                </thead>
                <tbody>
                  {accountArray ? (
                    accountArray.map((account, idx) => {
                      return (
                        <tr
                          className="border-t-2 border-gray-300"
                          key={"table_row_" + idx}
                        >
                          <td scope="col" className="px-6 py-3">
                            {account.billing_account_type}
                          </td>
                          <td scope="col" className="px-6 py-3">
                            {account.ley}
                          </td>
                          <td scope="col" className="px-6 py-3">
                            <button
                              onClick={(e) => {
                                onClickEdit(account);
                              }}
                              onKeyUp={() => {}}
                              className="success m-3 rounded-lg bg-principal-700 text-principal-150focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
                            >
                              <span className="flex">
                                Editar <IoMdOpen className="mx-2 h-5"/>
                              </span>
                            </button>

                            {account.estado == CUENTA_COBRO_STATE.rechazado && (
                              <button
                                onClick={(e) => {
                                  onClickRectify(account.billingAccountId);
                                }}
                                onKeyUp={() => {}}
                                className="success m-3 rounded-lg bg-principal-700 text-principal-150focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
                              >
                                <span className="flex">
                                  Subsanar <CgBandAid className="mx-2 h-5"/>
                                </span>
                              </button>
                            )}
                          </td>
                          <td scope="col" className="px-6 py-3">
                            <SpanMessage
                              type={getStatusColor(
                                account.estado
                                  ? account.estado
                                  : CUENTA_COBRO_STATUS.RECHAZADO
                              )}
                              text={account.estado ? account.estado : "Pending"}
                            />
                          </td>
                          <td scope="col" className="px-6 py-3">
                            <SpanMessage
                              type={getStatusColor(
                                account.analista
                                  ? account.analista
                                  : CUENTA_COBRO_STATUS.RECHAZADO
                              )}
                              text={
                                account.analista ? account.analista : "Pending"
                              }
                            />
                          </td>
                          <td scope="col" className="px-6 py-3">
                            <SpanMessage
                              type={getStatusColor(
                                account.jefatura
                                  ? account.jefatura
                                  : CUENTA_COBRO_STATUS.RECHAZADO
                              )}
                              text={
                                account.jefatura ? account.analista : "Pending"
                              }
                            />
                          </td>

                          {userRole.role === USER_ROLE.JEFATURA && (
                            <td scope="col" className="px-6 py-3">
                              <div className="flex justify-center w-[40px] h-[40px] min-w-[35px] min-h-[35px] w-full h-auto">
                                <a
                                  href={
                                    account?.document_word_url
                                      ? account?.document_word_url
                                      : "#"
                                  }
                                >
                                  <Image
                                    src="/brainiac/static/icons/word.png"
                                    alt="download Word"
                                    width={40}
                                    height={40}                                    
                                  />
                                </a>
                              </div>
                            </td>
                          )}

                          {userRole.role !== USER_ROLE.UNDEFINED && (
                            <td scope="col" className="px-6 py-3">
                              <div className="flex justify-center w-[50px] h-[50px] min-w-[40px] min-h-[40px] w-full h-auto">
                                <a
                                  href={
                                    account.document_excel_url
                                      ? account.document_excel_url
                                      : "#"
                                  }
                                >
                                  <Image
                                    src="/brainiac/static/icons/excel.png"
                                    alt="download Excel"
                                    width={50}
                                    height={50}                                    
                                  />
                                </a>
                              </div>
                            </td>
                          )}
                        </tr>
                      );
                    })
                  ) : (
                    <div></div>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
        {showStatusModal && (
          <ModalStatus
            title="Detalles de la cuenta"
            currentUser={userRole.name}
            currentRole={userRole.role}
            selectedAccount={clickAccount}
            titleClass="justify-left text-[1.5rem]"
            description={"description"}
            primaryButtonText="Aprobar"
            SecondaryButtonText="Rechazar"
            stateVisibleFn={setShowStatusModal}
            stateVisibleVar={showStatusModal}
            onPrimaryClick={() => {
              clickAccount ? processAcceptQuery(clickAccount) : () => {};
            }}
            onSecondaryClick={() => {
              clickAccount ? processRejectQuery(clickAccount) : () => {};
            }}
          />
        )}
      </div>
      {showProcessModal && (
        <ModalProcessAction
          title="Detalles de la cuenta"
          currentUser={userRole.name}
          currentRole={userRole.role}
          selectedAccount={clickAccount}
          titleClass="justify-left text-[1.5rem]"
          description={"description"}
          primaryButtonText="Aprobar"
          SecondaryButtonText="Rechazar"
          stateVisibleFn={setShowProcessModal}
          stateVisibleVar={showProcessModal}
          processAction={processAction}
          onPrimaryClick={() => {
            clickAccount ? confirmAcceptQuery(clickAccount) : () => {};
          }}
          onSecondaryClick={(reason: string) => {
            clickAccount ? confirmRejectQuery(clickAccount, reason) : () => {};
          }}
        />
      )}
      {showResultModal && (
        <ModalProcessResult
          title="Aprobación exitosa"
          titleClass="text-center text-[1.5rem]"
          primaryButtonText="Aceptar"
          stateVisibleFn={setShowResultModal}
          stateVisibleVar={showResultModal}
          processAction={processAction}
          onPrimaryClick={() => {
            //setShowStatusModal(false);
          }}
          onSecondaryClick={() => {
            //setShowStatusModal(false);
          }}
        />
      )}
    </div>
  );
};
