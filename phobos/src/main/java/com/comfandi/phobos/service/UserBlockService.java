package com.comfandi.phobos.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.request.ApiLoginRequest;
import com.comfandi.phobos.client.response.ApiDocumentGenerationResponse;
import com.comfandi.phobos.mapper.InformationDatabaseMapper;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserJsonPropertyDto;
import com.comfandi.phobos.service.enums.UserType;
import com.comfandi.phobos.util.ApiConfig;
import com.comfandi.phobos.util.Message;

@Service
public class UserBlockService {
	

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private ExternalServiceClient externalServiceClient;
    
    @Autowired
    private ApiConfig apiConfig;

    
    @Autowired
    private InformationDatabaseMapper informationDatabaseMapper;


    public List<UserDto> allUsers(){
        ApiLoginRequest request = ApiLoginRequest.builder()
            .username(apiConfig.userComfandi)
            .password(apiConfig.passComfandi)
            .build();

        String token = loginService.login(request);
        return getUsers(token);
    }

    public List<UserJsonPropertyDto> activeUsers() {
        try {
            
            List<UserDto> users = allUsers();

            return users.stream()
                    .filter(u -> UserType.fromValue(u.getRegional()) == UserType.ACTIVO)
                    //.filter(u -> "Sin Cobrar".equalsIgnoreCase(u.getCharge()))
                    .collect(Collectors.toMap(
                            UserDto::getIdentification,
                            u -> u,
                            (u1, u2) -> u1
                    ))
                    .values()
                    .stream()
                    .map(this::toJsonPropertyDto)
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<UserJsonPropertyDto> cesantesUsers() {
        try {
            ApiLoginRequest request = ApiLoginRequest.builder()
                    .username(apiConfig.userComfandi)
                    .password(apiConfig.passComfandi)
                    .build();

            String token = loginService.login(request);
            List<UserDto> users = getUsers(token);

            return users.stream()
                    .filter(u -> UserType.fromValue(u.getRegional()) == UserType.CESANTE)
                   // .filter(u -> "Sin Cobrar".equalsIgnoreCase(u.getCharge()))
                    .filter(u -> parseDoubleSafe(u.getProgress()) >= 30.0)
                    .collect(Collectors.toMap(
                            UserDto::getIdentification,
                            u -> u,
                            (u1, u2) -> u1
                    ))
                    .values()
                    .stream()
                    .map(this::toJsonPropertyDto)
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<UserDto> getUsers(String token) {
        return externalServiceClient.fetchUsuariosFromService(token);
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private UserJsonPropertyDto toJsonPropertyDto(UserDto user) {
        if (user == null) return null;

        return UserJsonPropertyDto.builder()
                .regional(user.getRegional())
                .program(user.getProgram())
                .standardProgram(user.getStandardProgram())
                .mode(user.getMode())
                .registration(user.getRegistration())
                .attentionLine(user.getAttentionLine())
                .startDate(user.getStartDate())
                .endDate(user.getEndDate())
                .identification(user.getIdentification())
                .identificationType(user.getIdentificationType())
                .fullName(user.getFullName())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .progress(user.getProgress())
                .withdrawal(user.getWithdrawal())
                .approval(user.getApproval())
                .charge(user.getCharge())
                .receipt(user.getReceipt())
                .userId(user.getUserId())
                .build();
    }

    public Map<String, List<UserJsonPropertyDto>> groupedByRegional(List<UserJsonPropertyDto> users) {

        return users.stream()
                .collect(Collectors.groupingBy(UserJsonPropertyDto::getRegional));
    }


    public List<ApiInformationDatabase> mapUsersToApiInfo(List<UserJsonPropertyDto> users) {
        return users.stream()
                .map(user -> {

                    ApiInformationDatabase apiInfo = ApiInformationDatabase.builder()
                        .userId(user.getUserId())
                        .nit(user.getBusinessId())
                        .companyName(user.getBusinessName())
                        .cityName(user.getRegionalResidencia())
                        .mode(user.getMode())
                        .identification(user.getIdentification())
                        .programName(user.getStandardProgram())
                        .fullName(user.getFullName())
                        .remissionDate(user.getFechaRemision())
                        .phoneNumber(user.getMobile())
                        .emailAddress(user.getEmail())
                        .fee(user.getCourseFee() != null ? user.getCourseFee().toString() : "")
                        .paymentRegion(Message.PAYMENT_REGION)
                        .contributingBeneficiaryObservation(Message.CONTRIBUTING_OBSERVATION)
                        .contributingIdNumber(user.getIdentification())
                        .programAdvancePercent(user.getProgress())
                        .startingProgramDate(user.getStartDate())
                        .endingProgramDate(user.getEndDate())
                        .templateType(Message.TEMPLATE_TYPE)
                        .charge(user.getCharge())
                        .observations(user.getDescription())
                        .portfolioId(user.getPortfolioId())
                        .programId(user.getProgramId())
                        .build();
                    return apiInfo;
                })
                .toList();
    }
    

    private ApiDocumentGenerationResponse sendUsersToKorlon(List<ApiInformationDatabase> users,String profile) {
        
        return externalServiceClient.fetchKorlonFromService(users,profile);
    }


    public void processUsersConvertedByRegional(UserType userType, List<UserJsonPropertyDto> users) {
        Map<String, List<UserJsonPropertyDto>> usersPorRegional = groupedByRegional(users);

        usersPorRegional.forEach((regional, listaUsuarios) -> {
            List<ApiInformationDatabase> convertidos = mapUsersToApiInfo(listaUsuarios);
            //TODO: Aqui esta la data. convertidos
            sendUsersToKorlon(convertidos, UserType.validateProfile(userType));
            System.out.println("Regional: " + regional + " => " + convertidos.size() + " usuarios convertidos");

        });
    }

    
    public void processActivateUsersByRegional(List<UserJsonPropertyDto> users){
        processUsersConvertedByRegional(UserType.ACTIVO, users);
    }

    public void processCesanteUsersByRegional(List<UserJsonPropertyDto> users){
        processUsersConvertedByRegional(UserType.CESANTE, users);
    }


}
