package com.comfandi.phobos.mapper;

import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.response.ApiUnavailableUsersResponse;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.WorkshopUserDto;
import com.comfandi.phobos.service.enums.Providers;
import com.comfandi.phobos.util.Message;
import org.springframework.stereotype.Component;

@Component
public class InformationDatabaseMapper {

    public  ApiInformationDatabase userDtoToApiInformationDatabase(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        ApiInformationDatabase apiInformationDatabase = ApiInformationDatabase
                .builder()
                .userId(userDto.getUserId())
                .nit(userDto.getBusinessId())
                .companyName(userDto.getBusinessName())
                .cityName(userDto.getRegionalResidencia())
                .mode(userDto.getMode())//todo: Add mode viene crea
                .identification(userDto.getIdentification())
                .programName(userDto.getStandardProgram())
                .fullName(userDto.getFullName())
                .remissionDate(userDto.getFechaRemision())
                .phoneNumber(userDto.getMobile())
                .emailAddress(userDto.getEmail())
                .fee(userDto.getCourseFee().toString())
                .paymentRegion(Message.PAYMENT_REGION)
                .contributingBeneficiaryObservation(Message.CONTRIBUTING_OBSERVATION)
                .contributingIdNumber(userDto.getIdentification())
                .programAdvancePercent(userDto.getProgress())
                .startingProgramDate(userDto.getStartDate())
                .endingProgramDate(userDto.getEndDate())
                .templateType(Message.TEMPLATE_TYPE)
                .charge(userDto.getCharge())
                .observations(userDto.getDescription())
                .portfolioId(userDto.getPortfolioId())
                .programId(userDto.getProgramId())
                .build();
        return apiInformationDatabase;
    }

    public  ApiInformationDatabase unavailableUsersResponseToApiInformationDatabase(ApiUnavailableUsersResponse userDto) {
        if (userDto == null) {
            return null;
        }
        ApiInformationDatabase apiInformationDatabase = ApiInformationDatabase
                .builder()
                .identification(userDto.getIdentification())
                .tipoDocumento(userDto.getIdentificationType())
                .remissionDate(userDto.getUnavailableDate())
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getMobile())
                .emailAddress(userDto.getEmail())
                .programAdvancePercent(userDto.getProgress())
                .startingProgramDate(userDto.getStartDate())
                .charge(userDto.getState())
                .observations(userDto.getDescription())
                .build();
        return apiInformationDatabase;
    }

    public ApiInformationDatabase revokedUsersToApiInformationDataBase(UserDto user){
        return ApiInformationDatabase
                .builder()
                .identification(user.getIdentification())
                .fullName(user.getFullName())
                .emailAddress(user.getEmail())
                .charge(user.getState())
                .observations(user.getDescription())
                .build();
    }

    public ApiInformationDatabase workshopUserDtoToApiInformationDatabase(WorkshopUserDto workshopUserDto){
        return ApiInformationDatabase.builder()
                .userId(workshopUserDto.getUserId())
                .tipoDocumento(workshopUserDto.getIdentificationType())
                .mode(workshopUserDto.getModalidad())
                .programName(workshopUserDto.getProgram())
                .identification(workshopUserDto.getIdentification())
                .cityName(workshopUserDto.getCity())
                .fullName(workshopUserDto.getFullName())
                .startingProgramDate(workshopUserDto.getStartDate())
                .fee(workshopUserDto.getCourseFee().toString())
                .emailAddress(workshopUserDto.getEmail())
                .charge(workshopUserDto.getState())
                .templateType(Message.TEMPLATE_TYPE)
                .paymentRegion(workshopUserDto.getCity())
                .startingProgramDate(workshopUserDto.getStartDate())
                .remissionDate(workshopUserDto.getOrientationDate())
                .provider(Providers.FOMENTO_TH_FOSFEC.getValue())
                .programAdvancePercent(workshopUserDto.getProgress())
                .endingProgramDate(workshopUserDto.getOrientationDate())
                .observations(workshopUserDto.getDescription()).build();
    }

}
