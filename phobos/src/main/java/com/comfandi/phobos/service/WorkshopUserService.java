package com.comfandi.phobos.service;

import com.comfandi.phobos.client.ExternalWorkshopServiceClient;
import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.request.ApiLoginRequest;
import com.comfandi.phobos.client.request.ApiUserRequest;
import com.comfandi.phobos.client.response.ApiDocumentGenerationResponse;
import com.comfandi.phobos.client.response.ApiWorkshopAppointmentsResponse;
import com.comfandi.phobos.entity.ModalityEntity;
import com.comfandi.phobos.entity.RegionalEntity;
import com.comfandi.phobos.entity.WorkshopEntity;
import com.comfandi.phobos.entity.WorkshopUserEntity;
import com.comfandi.phobos.mapper.InformationDatabaseMapper;
import com.comfandi.phobos.mapper.WorkshopMapper;
import com.comfandi.phobos.repository.ModalityRepository;
import com.comfandi.phobos.repository.WorkshopUserRepository;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.WorkshopUserDto;
import com.comfandi.phobos.service.enums.UserIdentificationType;
import com.comfandi.phobos.service.enums.UserType;
import com.comfandi.phobos.util.ApiConfig;
import com.comfandi.phobos.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkshopUserService {

    @Autowired
    private ExternalWorkshopServiceClient externalWorkshopServiceClient;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private LoginService loginService;

    @Autowired
    private WorkshopUserRepository workshopUserRepository;

    @Autowired
    private ModalityService modalityService;

    @Autowired
    private RegionalService regionalService;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private InformationDatabaseMapper informationDatabaseMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<WorkshopUserDto> getWorkshopsUsers() {
        try {
            ApiLoginRequest request = ApiLoginRequest.builder().username(apiConfig.userComfandi).password(apiConfig.passComfandi).build();
            String token = loginService.login(request);
            List<WorkshopUserDto> workshopUserList = externalWorkshopServiceClient.fetchWorkshopUsersFromService(token).stream()
                    .peek(this::setWorkshopFee).toList();
            System.out.println("Usuarios a validar "+workshopUserList.size());
            Flux<Tuple2<Boolean, WorkshopUserDto>> validations = validateAppointments(workshopUserList);
            List<WorkshopUserDto> confirmedUsers =  saveUsers(Objects.requireNonNull(validations.map(Tuple2::getT2).collectList().block()));

            System.out.println("Usuarios almacenados "+confirmedUsers.size());
            List<ApiInformationDatabase> apiInformationDatabaseList = confirmedUsers.stream()
                    .filter(x->x.getState().equals(Message.CHARGED)).map(informationDatabaseMapper::workshopUserDtoToApiInformationDatabase).toList();
            sendUsersToKorlon(apiInformationDatabaseList, UserType.validateProfile(UserType.TALLERES));

            return confirmedUsers;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void setWorkshopFee(WorkshopUserDto user){
        Optional<WorkshopEntity> optWorkshop = workshopService.getWorkshopByName(user.getProgram(), user.getYear());
        optWorkshop.ifPresent(workshopEntity -> user.setCourseFee(BigDecimal.valueOf(workshopEntity.getValue())));
    }

    public List<WorkshopUserDto> saveUsers(List<WorkshopUserDto> user) {
        List<ModalityEntity> modalityList = modalityService.getAllMModality();
        List<RegionalEntity> regionmalList = regionalService.getAllRegionals();
        List<WorkshopUserEntity> userEntityList = workshopUserRepository.saveAll(user.stream().map(x -> {
            ModalityEntity modality = modalityService.findByName(modalityList, x.getModalidad());
            RegionalEntity regional = regionalService.findByName(regionmalList, x.getRegional());
            Optional<WorkshopEntity> workshop = workshopService.getWorkshopByName(x.getProgram(), x.getYear());
            WorkshopUserEntity entity = WorkshopMapper.userDtoToWorkshopEntity(x);
            if (regional != null && modality != null && workshop.isPresent()) {
                entity.setModality(modality);
                entity.setRegional(regional);
                entity.setWorkshop(workshop.get());
                entity.setValue(workshop.get().getValue());
            }
            if(x.getState()==null){
                x.setState(Message.NOT_CHARGED);
            }
            return entity;
        }).toList());
        return userEntityList.stream().map(WorkshopMapper::workoshopEntityToWorkshopUserDto).toList();
    }


    public Boolean validatePreviousDates(WorkshopUserDto user) {
        Optional<WorkshopEntity> optWorkshop = workshopService.getWorkshopByName(user.getProgram(), user.getYear());
        if (optWorkshop.isPresent()) {
            List<WorkshopUserEntity> workshopUserList = workshopUserRepository.findRegistryBeforeDays(
                    user.getIdentification(), user.getIdentificationType(),
                    optWorkshop.get().getId(), LocalDate.parse(user.getOrientationDate(), formatter).minusDays(90));
            if (!workshopUserList.isEmpty()) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.WORKSHOP_FOUND_90_DAYS);
                return true;
            }
            return true;
        }
        user.setState(Message.NOT_CHARGED);
        user.setDescription(Message.WORKSHOP_NOT_FOUND);
        return false;
    }

    public Boolean validateOrientationDate(WorkshopUserDto user) {
        LocalDate startDate = LocalDate.parse(user.getStartDate());
        LocalDate orientationDate = LocalDate.parse(user.getOrientationDate());
        if (orientationDate.isAfter(startDate)) {
            user.setState(Message.NOT_CHARGED);
            user.setDescription(Message.ORIENTATION_DATE_MORE_THAN_START_DATE);
            return false;
        }
        return true;
    }

    private Flux<Tuple2<Boolean, WorkshopUserDto>> validateAppointments(List<WorkshopUserDto> users) {
        List<ApiUserRequest> ids = users.stream()
                .map(request -> new ApiUserRequest(request.getIdentification(),
                        UserIdentificationType.findIdentificationTypeByName(request.getIdentificationType())
                                .getAcronym()))
                .toList();
        return externalWorkshopServiceClient.fetchWorkshopAppointmentsFromService(ids, "token")
                .flatMapMany(response -> {
                    Map<String, ApiWorkshopAppointmentsResponse> result = response
                            .stream()
                            .collect(Collectors.toMap(ApiWorkshopAppointmentsResponse::getDocument, Function.identity()));
                    List<Tuple2<Boolean, WorkshopUserDto>> firstValidation = new ArrayList<>();
                    for (WorkshopUserDto user : users) {
                            ApiWorkshopAppointmentsResponse appointment = result.get(user.getIdentification());
                            if (appointment != null) {
                                user.setOrientationDate(appointment.getAppointmentDate());
                                if (appointment.getStatusAppointment().equals(Message.APPOINTMENT_SUCCESS)) {
                                    if (validatePreviousDates(user)) {
                                        if (validateOrientationDate(user)) {
                                            user.setState(Message.CHARGED);
                                            user.setDescription("OK");
                                            firstValidation.add(Tuples.of(true, user));
                                        } else {
                                            firstValidation.add(Tuples.of(false, user));
                                        }
                                    } else {
                                        firstValidation.add(Tuples.of(false, user));
                                    }

                                } else {
                                    user.setState(Message.NOT_CHARGED);
                                    user.setDescription(Message.APPOINTMENT_NOT_SUCCESS);
                                    firstValidation.add(Tuples.of(false, user));
                                }
                            } else {
                                user.setState(Message.NOT_CHARGED);
                                user.setDescription(Message.APPOINTMENT_NOT_FOUND);
                                firstValidation.add(Tuples.of(false, user));
                            }


                    }
                    return Flux.fromIterable(firstValidation);
                });
    }


    private ApiDocumentGenerationResponse sendUsersToKorlon(List<ApiInformationDatabase> users, String profile) {
        return externalWorkshopServiceClient.fetchKorlonFromService(users, profile);
    }


}
