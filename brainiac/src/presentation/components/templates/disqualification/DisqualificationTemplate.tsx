"use client";

import {
  NotAceptableUser,
  NotAceptableUserList,
  NotAceptableUsersResponse,
  QueryDisqualifiedUsers,
  USER_ROLE,
} from "@/lib";
import { TextSearchInput } from "@/presentation/components/atoms/common/input/TextSearchInput";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { IoMdOpen } from "react-icons/io";
import { RxCounterClockwiseClock } from "react-icons/rx";
import {
  Disqualification,
  DisqualificationList,
} from "@/lib/types/cuentas.types";
import { useSession } from "next-auth/react";
import { jwtDecode } from "jwt-decode";
import { IUserToken } from "@/types";
import { LoggedUserInfo } from "@/types/status";
import Link from "next/link";
import { appContainer } from "@/infrastructure";
import DisqualifiedUserListUseCase from "@/domain/usecases/accounts/disqualifiedUserList.usecase";
import { USECASES_TYPES } from "@/infrastructure/ioc/containers/usecases/usecases.types";

export const DisqualificationPageTemplate = () => {
  const [disqualificationArray, setDisqualificationArray] =
    useState<NotAceptableUserList>();
  const [disqualificationCompleteArray, setDisqualificationCompleteArray] =
    useState<NotAceptableUserList>();

  const [clickDisqualification, setClickDisqualification] =
    useState<NotAceptableUser>();
  const [showStatusModal, setShowStatusModal] = useState(false);

  const [userRole, setUserRole] = useState<LoggedUserInfo>({
    name: "Usuario",
    role: USER_ROLE.UNDEFINED,
  });

  const [menuIsOpen, setMenuIsOpen] = useState(false);

  const openMenu = () => {
    setMenuIsOpen(!menuIsOpen);
  };

  const { data: session } = useSession();

  // Search input manage
  const searchFormRef = useRef<HTMLFormElement>(null);

  const onChangeSearch = (value: string) => {
    console.log("search string:", value);
  };

  const onClickEdit = (disqualification: NotAceptableUser) => {
    setClickDisqualification(disqualification);
  };

  const loadDisqualificationsArray = async () => {
    const token = session?.access_token ? session?.access_token : "";

    const queryData = {
      startDate: "2025-01-01",
      endDate: "2025-12-31",
    } as QueryDisqualifiedUsers;

    const queryDisqualifiedUsersUser =
      appContainer.get<DisqualifiedUserListUseCase>(
        USECASES_TYPES._DisqualifiedUserListUseCase
      );

    let response = await queryDisqualifiedUsersUser.execute(queryData, token);
    console.log("Rectify result:", response);

    if (!response) {
      const users = [
        {
          //id: "349da283-3740-4198-8176-129a32f0ca36",
          //documentType: "CC",
          //documentNumber: "123456790",
          Nombre_completo: "Carlos Andres",
          Causa: "Documentación invalida",
          Avance: "",
          //Causa:"",
          Celular: "",
          Email: "",
          Fec_inhabilitacion: "",
          Fec_inicio: "",
          Identificacion: "123456790",
          Tipo_identificacion: "CC",
          //createdAt: "1/1/2025",
        },
        {
          //id: "349da283-3740-4198-8176-129a32f0ca37",
          //documentType: "CC",
          //documentNumber: "123456790",
          Nombre_completo: "Pedro Campo",
          Causa: "Usuario reportado",
          Avance: "",
          //Causa:"",
          Celular: "",
          Email: "",
          Fec_inhabilitacion: "",
          Fec_inicio: "",
          Identificacion: "123456791",
          Tipo_identificacion: "CC",
          //createdAt: "1/1/2025",
        },
        {
          //id: "349da283-3740-4198-8176-129a32f0ca38",
          //documentType: "CC",
          //documentNumber: "123456790",
          Nombre_completo: "Diego Calderón",
          Causa: "Validación pendiente",
          Avance: "",
          //Causa:"",
          Celular: "",
          Email: "",
          Fec_inhabilitacion: "",
          Fec_inicio: "",
          Identificacion: "123456792",
          Tipo_identificacion: "CC",
          //createdAt: "1/1/2025",
        },
      ] as NotAceptableUserList;

      response = {
        data: {
          users: users,
          page: 0,
          size: 100,
          totalElements: 1000,
          totalPages: 10,
        },
      } as NotAceptableUsersResponse;
    }

    // used in UI (Filter)
    setDisqualificationArray(response);
    // complete list, not modify
    setDisqualificationCompleteArray(response);
  };

  const getUserRole = () => {
    let userInfo: IUserToken = jwtDecode(session?.access_token!);

    if ("scope" in userInfo) {
      const scopeList = userInfo.scope.split(" ");

      // EXAMPLE SEARCH ON JWT
      if (scopeList.find((e) => e === "identification")) {
        console.log("Analista ROL");
        setUserRole({ name: userInfo.given_name, role: USER_ROLE.ANALISTA });
      }

      if (scopeList.find((e) => e === "digital_identity")) {
        console.log("Jefatura ROL");
        //setUserRole({ name: userInfo.given_name, role: USER_ROLE.JEFATURA });
      }
    }
    //console.log(userInfo);
  };

  useEffect(() => {
    loadDisqualificationsArray();
    getUserRole();
  }, []);

  return (
    <div className="block w-full h-full overflow-y-auto">
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
                          href={"/"}
                          className="block px-4 py-2 text-sm text-gray-700 data-[focus]:bg-gray-100 data-[focus]:text-gray-900 data-[focus]:outline-none"
                        >
                          Inicio
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
            Proceso Inhabilitaciones
          </p>
        </div>
      </div>

      <div className="block px-[40px] mt-5">
        <div className="rounded-lg shadow-md border-[1px] bg-white px-10">
          <div className="block md:flex my-10">
            <p className="font-bold mx-3 text-[2rem] flex">
              <RxCounterClockwiseClock />{" "}
              <span className="mx-2 mt-[-8px] w-[500px]">
                Registro de Inhabilitaciones
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
                  <th>Tipo de documento</th>
                  <th>No de documento</th>
                  <th>Nombre</th>
                  <th>Motivo de inhabilitación</th>
                  {/*<th>Acción</th>*/}
                </tr>
              </thead>
              <tbody>
                {disqualificationArray ? (
                  disqualificationArray.map((disqualification, idx) => {
                    return (
                      <tr
                        className="border-t-2 border-gray-300"
                        key={"table_row_" + idx}
                      >
                        <td scope="col" className="px-6 py-3">
                          {disqualification.Tipo_identificacion}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {disqualification.Identificacion}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {disqualification.Nombre_completo}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {disqualification.Causa}
                        </td>
                        {/*
                        <td scope="col" className="px-6 py-3">
                          <button
                            onClick={(e) => {
                              onClickEdit(disqualification);
                            }}
                            onKeyUp={() => {}}
                            className="success m-3 rounded-lg bg-principal-700 text-principal-150focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
                          >
                            <span className="flex">
                              Ver<IoMdOpen className="ml-2" />
                            </span>
                          </button>
                        </td>
                        */}
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
    </div>
  );
};
