"use client";

import { USER_ROLE } from "@/lib";
import { TextSearchInput } from "@/presentation/components/atoms/common/input/TextSearchInput";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import {
  Disqualification,
  DisqualificationList,
  QueryRectifyListRevokedUser,
  QueryRectifyRevokedUser,
  RevokedUser,
  RevokedUserList,
} from "@/lib/types/cuentas.types";
import { useSession } from "next-auth/react";
import { jwtDecode } from "jwt-decode";
import { IUserToken } from "@/types";
import { LoggedUserInfo } from "@/types/status";
import Link from "next/link";
import { appContainer } from "@/infrastructure";
import RevokedUserListUseCase from "@/domain/usecases/accounts/revokedUserList.usecase";
import { USECASES_TYPES } from "@/infrastructure/ioc/containers/usecases/usecases.types";
import RectifyRevokedUserUseCase from "@/domain/usecases/accounts/rectifyRevokedUser.usecase";

import { RxCounterClockwiseClock } from "react-icons/rx";
import { CgBandAid } from "react-icons/cg";
import { PiMicrosoftExcelLogoFill } from "react-icons/pi";
import { ModalProcessConfirmation } from "../../atoms/common/modal/ModalProcessConfirmation";
import { Button, FileInput, ModalWithChildren } from "../../atoms";
import { toast } from "react-toastify";
import RectifyRevokedUserListUseCase from "@/domain/usecases/accounts/rectifyRevokedUserList.usecase";

interface RectifyProps {
  id: string;
}

export const RectifyAccountPageTemplate: React.FC<RectifyProps> = (props) => {
  console.log("Received props: ", props);

  const MODAL_RECTIFY_MSG =
    "¿Esta seguro que desea actualizar el estado de este usuario?";

  const [revokedUsers, setRevokedUsers] = useState<RevokedUserList>();
  const [disqualificationCompleteArray, setDisqualificationCompleteArray] =
    useState<DisqualificationList>();

  const [clickDisqualification, setClickDisqualification] =
    useState<RevokedUser>();
  const [showStatusModal, setShowStatusModal] = useState(false);

  const [userRole, setUserRole] = useState<LoggedUserInfo>({
    name: "Usuario",
    role: USER_ROLE.UNDEFINED,
  });

  const [menuIsOpen, setMenuIsOpen] = useState(false);

  // APPROVE MODAL
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState("");
  const [approveStatus, setApproveStatus] = useState(true);

  // FILE MODAL
  const [openModal, setOpenModal] = useState(false);
  const [file, setFile] = useState<File | null>(null);

  const openMenu = () => {
    setMenuIsOpen(!menuIsOpen);
  };

  const { data: session } = useSession();

  // Search input manage
  const searchFormRef = useRef<HTMLFormElement>(null);

  const onChangeSearch = (value: string) => {
    console.log("search string:", value);
  };

  const onClickRectify = (userId: string) => {
    rectifyRevokedUser(userId);
  };

  const onClickProcessList = () => {
    setOpenModal(true);
  };

  const handleBulkLoading = async () => {
    const token = session?.access_token ? session?.access_token : "";
    if (!file) {
      toast.error("Por favor, selecciona un archivo para cargar.");
      return;
    }

    const query = {
      file: file,
      accountId: +props.id,
    } as QueryRectifyListRevokedUser;

    const bulkScheduleLoading = appContainer.get<RectifyRevokedUserListUseCase>(
      USECASES_TYPES._RectifyRevokedUserListUseCase
    );

    const response: any = await bulkScheduleLoading.execute(query, token);
    if (response?.error) {
      toast.error(response.message || "Error al cargar los cronogramas.", {
        autoClose: 6000,
      });
    } else {
      toast.success("Listado cargado correctamente!");
    }
    setOpenModal(false);
    setFile(null);
  };

  const loadRevokedUserArray = async () => {
    const queryData = {
      accountId: props.id,
    };
    const token = session?.access_token ? session?.access_token : "";
    const queryRevokedUsers = appContainer.get<RevokedUserListUseCase>(
      USECASES_TYPES._RevokedUserListUseCase
    );

    let response = await queryRevokedUsers.execute(queryData, token);
    console.log("List result:", response);

    // Use mock data if no backend
    if (!response) {
      console.error("Cant read the server, result:", response);
      //return;

      response = {
        page: 0,
        size: 50,
        totalElements: 100,
        totalPages: 1,
        data: [
          {
            Date: "2025-04-15T01:52:00.543166",
            Full_name: "Juan Fernando Lopez Gomez",
            Identification_number: "1234567890",
            Reason: "Cuenta mal diligenciada",
            Status: "Revocado",
            Account: "999",
          },
          {
            Date: "2025-04-15T03:52:00.543166",
            Full_name: "Diana Maria Gomez Losada",
            Identification_number: "1234567891",
            Reason: "Documentación incompleta",
            Status: "Revocado",
            Account: "999",
          },
        ],
      };
    }

    setRevokedUsers(response.data);
  };

  const rectifyRevokedUser = async (userId: string) => {
    const queryData = {
      accountId: +props.id,
      identificationNumber: +userId,
    } as QueryRectifyRevokedUser;

    const token = session?.access_token ? session?.access_token : "";
    const queryRectifyRevokedUser = appContainer.get<RectifyRevokedUserUseCase>(
      USECASES_TYPES._RectifyRevokedUserUseCase
    );

    let response = await queryRectifyRevokedUser.execute(queryData, token);
    console.log("Rectify result:", response);

    // Use mock data if no backend
    if (!response) {
      console.error("Cant read the server, result:", response);
      //return;
    }
    console.log(response);
    //Refresh List
    loadRevokedUserArray();
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
    loadRevokedUserArray();
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

          <p className="mt-2 text-[#05C3DD] text-lg">Proceso de SubSanación</p>
        </div>
      </div>

      <div className="block px-[40px] mt-5">
        <div className="rounded-lg shadow-md border-[1px] bg-white px-10">
          <div className="block md:flex my-10">
            <p className="font-bold mx-3 text-[2rem] flex">
              <RxCounterClockwiseClock />{" "}
              <span className="mx-2 mt-[-8px] w-[500px]">
                Usuarios con observaciones.
              </span>
            </p>

            {/*<div className="flex justify-end w-full mr-5">
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
            </div>*/}
            <div className="flex justify-end w-full mr-5">
              <button
                onClick={(e) => {
                  onClickProcessList();
                }}
                onKeyUp={() => {}}
                className="success m-3 rounded-lg bg-principal-700 text-principal-150focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
              >
                <span className="flex">
                  Procesar listado CSV{" "}
                  <PiMicrosoftExcelLogoFill className="mx-2 h-5" />
                </span>
              </button>
            </div>
          </div>

          <div className="block text-center overflow-y-auto">
            {/**border-2 border-dotted border-[#000] */}

            <table className="table-auto w-full text-align-center">
              <thead>
                <tr className="border-t-gray-700 text-gray-500">
                  <th>Nombre</th>
                  <th>No de documento</th>
                  <th>Razón</th>
                  <th>Estado</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {revokedUsers && revokedUsers.length >= 1 ? (
                  revokedUsers.map((revokedUser, idx) => {
                    return (
                      <tr
                        className="border-t-2 border-gray-300"
                        key={"table_row_" + idx}
                      >
                        <td scope="col" className="px-6 py-3">
                          {revokedUser.Full_name}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {revokedUser.Identification_number}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {revokedUser.Reason}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          {revokedUser.Status}
                        </td>
                        <td scope="col" className="px-6 py-3">
                          <button
                            onClick={(e) => {
                              setModalMessage(MODAL_RECTIFY_MSG);
                              setClickDisqualification(revokedUser);
                              setIsModalOpen(true);
                            }}
                            onKeyUp={() => {}}
                            className="success m-3 rounded-lg bg-principal-700 text-principal-150focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
                          >
                            <span className="flex">
                              Subsanar <CgBandAid className="mx-2 h-5" />
                            </span>
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr
                    className="border-t-2 border-gray-300"
                    key={"table_row_" + 0}
                  >
                    <td scope="col" colSpan={5} className="px-6 py-3">
                      {"No hay registros disponibles."}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      {isModalOpen && (
        <ModalProcessConfirmation
          title={"¡Solicitud de confirmación!"}
          message={modalMessage}
          onPrimaryClick={(reason: string) => {
            setIsModalOpen(false);
            if (approveStatus && clickDisqualification) {
              onClickRectify(clickDisqualification.Identification_number);
            }
          }}
          onSecondaryClick={() => {}}
          onClickClose={() => {
            setIsModalOpen(false);
            setClickDisqualification(undefined);
          }}
          titleClass="text-center text-[1.5rem]"
          primaryButtonText="Aceptar"
          currentStatus={approveStatus}
        />
      )}

      {openModal && (
        <ModalWithChildren
          onClose={() => setOpenModal(false)}
          className={`md:w-[463px] rounded-[20px] flex flex-col gap-5 items-center shadow-lg bg-principal-150 subpixel-antialiased`}
        >
          <Image
            src="/brainiac/static/icons/excel_icon.svg"
            alt="Close icon"
            width={80}
            height={80}
            priority
          />
          <h4 className="font-outfit text-[1rem] text-center font-semibold mb-2 pt-6 text-principal-180 mt-2">
            ¡Selecciona el archivo con los registros!
          </h4>
          <FileInput file={file} setFile={setFile} acceptedFileTypes=".csv" />
          <div className="flex flex-col w-[70%] space-y-3 py-2">
            <Button
              label="Continuar"
              onClick={handleBulkLoading}
              className="w-full"
              primary
            />
          </div>
        </ModalWithChildren>
      )}
    </div>
  );
};
