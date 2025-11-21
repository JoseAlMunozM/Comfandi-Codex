package com.comfandi.phobos.service;

import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.request.ApiUserRequest;
import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiLoginRequest;
import com.comfandi.phobos.client.response.*;
import com.comfandi.phobos.entity.CourseEntity;
import com.comfandi.phobos.entity.RegionalEntity;
import com.comfandi.phobos.entity.UserEntity;
import com.comfandi.phobos.exception.GenericException;
import com.comfandi.phobos.mapper.InformationDatabaseMapper;
import com.comfandi.phobos.mapper.UserMapper;
import com.comfandi.phobos.repository.LastUserLoginRepository;
import com.comfandi.phobos.repository.UserRepository;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.UserProcessingResult;
import com.comfandi.phobos.service.enums.UserIdentificationType;
import com.comfandi.phobos.service.enums.UserType;
import com.comfandi.phobos.util.ApiConfig;
import com.comfandi.phobos.util.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import java.time.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class UserProcessingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LastUserLoginRepository lastUserLogin;

    @Autowired
    private BillingAccountServices billingAccountServices;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RegionalService regionalService;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InformationDatabaseMapper informationDatabaseMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // @Scheduled(fixedRate = 60000) // Se ejecuta cada minuto
    public List<UserDto> processUsers() {
        try {
            ApiLoginRequest request = ApiLoginRequest.builder().username(apiConfig.userComfandi).password(apiConfig.passComfandi).build();
            String token = loginService.login(request);
            List<UserDto> users = getUsers(token);
            List<UserDto> usersWhitTariff = assignTariffToUsers(users);
            List<UserDto> usersWithOutProgram=usersWhitTariff.stream().filter(x-> !x.isExistingProgram()).toList();
            System.out.println("-- Start Service -------------------------");

            UserProcessingResult filteredUsers = filterDuplicates(usersWhitTariff.stream().filter(UserDto::isExistingProgram).toList());
            System.out.println("-- Filter users -------------------------" + filteredUsers);
            UserProcessingResult filterUsers = filterUsers(filteredUsers);
            System.out.println("-- Validate users -------------------------" + filterUsers);
            Map<String, UserEntity> mapUsersValidated = saveUsers(filterUsers.getUsersToValidate());
            System.out.println("-- Map users validated -------------------------" + mapUsersValidated);
            Map<String, UserEntity> mapUsersExisting = saveUsers(filterUsers.getExistingUsers());
            System.out.println("-- Map users existing -------------------------" + mapUsersExisting);
            filterUsers.getUsersToValidate().forEach(user -> {
                UserEntity userEntity = mapUsersValidated.get(user.getIdentification());
                if(userEntity != null) {
                    user.setUserId(userEntity.getId());
                }

            });
            System.out.println("-- For each user -------------------------");
            filterUsers.getExistingUsers().forEach(user -> {
                UserEntity userEntity=mapUsersExisting.get(user.getIdentification());
                if(userEntity != null) {
                    user.setUserId(userEntity.getId());
                }
            });
            System.out.println("-- For each existing user -------------------------");
            List<UserDto> allUsers = new ArrayList<>(filterUsers.getUsersToValidate());
            allUsers.addAll(filterUsers.getExistingUsers());
            System.out.println("-- Save users -------------------------" + allUsers);
            //todo: separar los perfiles para generar diferentes valores
            Map<String,List<UserDto>> profiles = allUsers.stream().collect(Collectors.groupingBy(UserDto::getRegional));
            System.out.println("-- Group users -------------------------" + profiles);
            for (Map.Entry<String, List<UserDto>> entry : profiles.entrySet()) {
                String regional = entry.getKey();
                List<UserDto> usersList = entry.getValue();
                UserType userType = UserType.fromValue(regional);
                System.out.println("-- User type -------------------------" + userType);
                if (userType != UserType.DEFAULT) {
                        List<ApiInformationDatabase> apiInformationDatabaseList = usersList
                            .stream()
                            .map(x -> {
                                ApiInformationDatabase aid= informationDatabaseMapper.userDtoToApiInformationDatabase(x);
                                CourseEntity course= courseService.findCourseByNames(x.getStandardProgram(),x.getMode().toUpperCase(),apiConfig.getYearSelected());
                                if( course!=null){
                                    aid.setProvider(course.getProvider().getName());
                                }
                                return  aid;
                            })

                            .toList();
                    sendUsersToKorlon(apiInformationDatabaseList, UserType.validateProfile(userType));
                    System.out.println("-- Enviado a korlon -------------------------");
                }
            }
            System.out.println("-- Process users -------------------------");
            List<UserDto> revokedUsers=allUsers.stream().filter(x-> x.getState().equals(Message.NOT_CHARGED)).toList();
            try{
                externalServiceClient.fetchRevokedNotificationFromService(
                        revokedUsers.stream()
                        .map(x-> informationDatabaseMapper.revokedUsersToApiInformationDataBase(x))
                        .toList(),
                        apiConfig.getEmailNotification());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("-- Finalize function -------------------------");
            return allUsers;
        } catch (Exception e) {
            System.err.println("Error en processUsers: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Fallo procesando usuarios", e);
        }
    }

    private List<UserDto> getUsers(String token) {
        return externalServiceClient.fetchUsuariosFromService(token);
    }

    public List<UserDto> assignTariffToUsers(List<UserDto> users) {
        CourseEntity course;
        for (UserDto user : users) {
            course=courseService.findCourseByNames(user.getStandardProgram(),user.getMode().toUpperCase(),apiConfig.getYearSelected());
            if(course!=null){
                user.setCourseFee(BigDecimal.valueOf(course.getValue()));
                if(course.getPortfolio()!=null){
                    user.setPortfolioId(course.getPortfolio().getId().toString());
                    user.setProgramId(course.getId().toString());
                }
                user.setExistingProgram(true);
            }else{
                user.setExistingProgram(false);
            }
        }
        return users;
    }

    private UserProcessingResult filterDuplicates(List<UserDto> users) {
        List<UserDto> uniqueUsers = new ArrayList<>();
        List<UserDto> duplicatedUsers = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();

        for (UserDto user : users) {
            String key = user.getIdentification() + "-" + user.getStandardProgram();

            if (uniqueKeys.contains(key)) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.DESCRIPTION_DUPLICATE_PERSON);
                duplicatedUsers.add(user);
            } else {
                uniqueKeys.add(key);
                uniqueUsers.add(user);
            }
        }

        return new UserProcessingResult(uniqueUsers, duplicatedUsers);
    }

    private UserProcessingResult filterUsers(UserProcessingResult users) {
        try {
            System.out.println("-- Inicio funcion filtro -------------------------");
            List<UserDto> usersToValidate = users.getUsersToValidate();
            System.out.println("-- paso 2 filtro -------------------------");
            Flux<UserDto> fluxUsersToValidate = Flux.fromIterable(usersToValidate);
            System.out.println("-- paso 3 filtro -------------------------");
            //se usa webflux para utilizar concurrencia en el llamado a los servicios y se define concurrencia de 100 (5) hilos
            //todo: validar en pruebas cuanto puede ser la cantidad de procesos en paralelo para ejecturar en paralelo
            Flux<Tuple2<Boolean, UserDto>> validations = fluxUsersToValidate
                .groupBy(user -> {
                    return UserType.fromValue(user.getRegional());
                })
                .flatMap(groupedFlux -> {
                    return switch (groupedFlux.key()) {
                        //Usuarios tipo activo con regional valle
                        case ACTIVO -> processActiveUsers(groupedFlux);
                        //Usuarios con tipo cesante
                        //case CESANTE -> validateLaidUsers(groupedFlux);
                        case CESANTE -> Flux.empty();
                        case DEFAULT,TALLERES -> Flux.empty();
                    };
                }, 5);
            System.out.println("-- paso 4 flux filtro -------------------------");
            //se separan las listas entre cumplen o no cumplen
            Mono<Tuple2<List<UserDto>, List<UserDto>>> resultList = validations
                .collect(() -> Tuples.of(new ArrayList<UserDto>(), new ArrayList<UserDto>()), (tuple, entry) -> {
                    if (entry != null) {
                        boolean passed = entry.getT1(); // evita NPE aquí
                        if (passed) {
                            //cumplen las validaciones
                            tuple.getT1().add(entry.getT2());
                        } else {
                            //no cumplen las validaciones
                            tuple.getT2().add(entry.getT2());
                        }
                    }
                });
            System.out.println("-- paso 5 filtro -------------------------");
            System.out.println("-- Var 1 ------------------------- " + resultList);
            System.out.println("-- Var 2 ------------------------- " + validations);
            Tuple2<List<UserDto>, List<UserDto>> userProcessingResultTuple = resultList.block();
            System.out.println("-- paso 6 filtro -------------------------");
            return new UserProcessingResult(userProcessingResultTuple != null ? userProcessingResultTuple.getT1() : Collections.emptyList(),
                userProcessingResultTuple != null ? userProcessingResultTuple.getT2() : Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error en filterUsers: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Fallo procesando usuarios", e);
        }
    }

    private Flux<Tuple2<Boolean, UserDto>> validateLaidUsers(GroupedFlux<UserType, UserDto> groupedFlux) {
        return groupedFlux.collectList()
                .flatMapMany(laidOffUsers -> {
                    List<ApiUserRequest> ids = laidOffUsers.stream()
                            .map(request -> new ApiUserRequest(request.getIdentification(),
                                    UserIdentificationType.findIdentificationTypeByName(request.getIdentificationType())
                                    .getAcronym()))
                            .toList();
                    ApiLoginResponse apiLoginResponse= externalServiceClient.loginPostulationFromService(apiConfig.getUserSubsidioComfandi(), apiConfig.getPassSubsidioComfandi());
                    return externalServiceClient.fetchUserStateFromService(ids,apiLoginResponse.getToken())
                            .flatMapMany(response -> {
                                        Map<String, String> stateMap = response.stream()
                                                .collect(Collectors.toMap(ApiUserStateResponse::getDocument, ApiUserStateResponse::getPostulationState));
                                        List<UserDto> firstValidUsers = new ArrayList<>();
                                        List<Tuple2<Boolean, UserDto>> failedFirstValidation = new ArrayList<>();
                                        for (UserDto user : laidOffUsers) {
                                            String state = stateMap.get(user.getIdentification());
                                            boolean valid = state != null && (
                                                    state.equalsIgnoreCase("Beneficio asignado") ||
                                                            state.equalsIgnoreCase("Beneficio asignado de capacitación")
                                            );
                                            if (valid) {
                                                firstValidUsers.add(user);
                                            } else {
                                                user.setState(Message.NOT_CHARGED);
                                                user.setDescription(Message.DESCRIPTION_NON_REFERRED_PERSON);
                                                failedFirstValidation.add(Tuples.of(false, user));
                                            }
                                        }
                                        List<ApiUserRequest> secondIds = firstValidUsers.stream()
                                                .map(user -> new ApiUserRequest(user.getIdentification(),
                                                        UserIdentificationType.findIdentificationTypeByName(user.getIdentificationType())
                                                                .getAcronym()))
                                                .toList();


                                        return externalServiceClient.fetchUsersResidenceFromService(secondIds)
                                                .flatMapMany(response2 -> {
                                                    Map<String, ApiUserResidenceResponse> secondMap = response2.stream()
                                                            .collect(Collectors.toMap(ApiUserResidenceResponse::getDocument, Function.identity()));
                                                    List<Tuple2<Boolean, UserDto>> finalResults = new ArrayList<>(failedFirstValidation);
                                                    for (UserDto user : firstValidUsers) {
                                                        ApiUserResidenceResponse info = secondMap.get(user.getIdentification());

                                                        if (info != null && info.getRegisterDate() != null && !info.getRegionalResidence().isEmpty()) {
                                                            user.setFechaRemision(info.getRegisterDate());
                                                            user.setRegionalResidencia(info.getRegionalResidence());
                                                            if(validateRemissionDateWithStartingDate(user)){
                                                                if(validateAssistance(user)){
                                                                    user.setState(Message.CHARGED);
                                                                    user.setDescription("OK");
                                                                    finalResults.add(Tuples.of(true, user));
                                                                }else{
                                                                    finalResults.add(Tuples.of(false, user));
                                                                }
                                                            }else{
                                                                finalResults.add(Tuples.of(false, user));
                                                            }

                                                        } else {
                                                            user.setState(Message.NOT_CHARGED);
                                                            user.setDescription("No se obtuvo información adicional válida");
                                                            finalResults.add(Tuples.of(false, user));
                                                        }
                                                    }
                                                    return Flux.fromIterable(finalResults);
                                                });
                                    }
                            );
                });
    }
    private Flux<Tuple2<Boolean, UserDto>> processActiveUsers(GroupedFlux<UserType, UserDto> groupedFlux){
        System.out.println("-- Inicio proceso usuarios activos -------------------------");
        Flux<Tuple2<Boolean, UserDto>> data= groupedFlux.flatMap(user -> validateActiveUser(user)
                .map(res -> Tuples.of(res, user)));
        System.out.println("-- Proceso usuarios activos -------------------------");
        return data.collectList()
                .flatMapMany(tupleList -> {
                    List<ApiUserRequest> requestList= tupleList.stream()
                            .map( x -> new ApiUserRequest(x.getT2().getIdentification(),x.getT2().getIdentificationType()))
                            .toList();
                        return  externalServiceClient.fetchBusinessIdentifierFromService(requestList,apiConfig.getApikeyPersonas())
                                .flatMapMany(businessResponse -> {
                                    Map<String, ApiBusinessUserResponse> mapResponse = businessResponse.stream()
                                            .collect(Collectors
                                                    .toMap(r-> r.getDocumentType()+":"+r.getDocument(),Function.identity()));
                                  List<Tuple2<Boolean,UserDto>> result = tupleList.stream()
                                          .map(tuple -> {
                                              Boolean flag = tuple.getT1();
                                              UserDto userDto= tuple.getT2();
                                              String key =   UserIdentificationType.findIdentificationTypeByName(userDto.getIdentificationType())
                                                      .getAcronym() +":"+userDto.getIdentification();
                                              ApiBusinessUserResponse businessData= mapResponse.get(key);
                                              if(businessData!=null){
                                                  if(!businessData.getBusinessName().equals("not_found")){
                                                      System.out.println("business ID "+businessData.getBusinessId());
                                                      userDto.setBusinessId(businessData.getBusinessId());
                                                      userDto.setBusinessName(businessData.getBusinessName());
                                                  }
                                              }
                                              return Tuples.of(flag,userDto);
                                          }).toList();
                                  return Flux.fromIterable(result);
                                });
                        });
    }

    private Mono<Boolean> validateActiveUser(UserDto user) {
        Date lastLoginDate = lastUserLogin.lastUserLogin(user.getIdentification());
        if (lastLoginDate == null) {
            user.setState(Message.NOT_CHARGED);
            user.setDescription(Message.DESCRIPTION_LAST_LOGIN);
            return Mono.just(false);
        }
        LocalDate lastLoginDatePlusOneMonth = lastLoginDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusMonths(1);

        if (!lastLoginDatePlusOneMonth.isAfter(LocalDate.parse(user.getStartDate()))) {
            user.setState(Message.NOT_CHARGED);
            user.setDescription(Message.DESCRIPTION_LAST_LOGIN);
            return Mono.just(false);

        }
        Optional<Boolean> userExists = userValidationLessThreeYears(user);
        if (userExists.isPresent()) {
            user.setState(Message.NOT_CHARGED);
            user.setDescription(Message.CHARGED_UNDER_THREE_YEARS_DESCRIPTION);
            return Mono.just(false);
        }
        user.setState(Message.CHARGED);
        user.setDescription("OK");
        return Mono.just(true);
    }

    private Long userValidationBillingThreeYears(UserDto user) {
        LocalDateTime localDateToValidate = LocalDateTime.now().minusYears(3);
        return billingAccountServices.findByUserAndDate(Long.valueOf(user.getIdentification()), localDateToValidate);
    }

    private boolean validateRemissionDateWithStartingDate(UserDto user) {

        if (user.getFechaRemision()!=null && user.getStartDate()!=null) {
            LocalDate startDate = LocalDate.parse(user.getStartDate(),formatter);
            LocalDate remissionDate = LocalDate.parse(user.getFechaRemision(),formatter);
            long days = ChronoUnit.DAYS.between(remissionDate,startDate);
            if(days>90) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.NOT_CHARGED_REMISSION_START_DATES);
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean validateAssistance(UserDto user) {

        if (user.getEndDate() == null || user.getStartDate().isBlank()) {
            if (user.getProgress() == null || user.getProgress().isBlank()) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.NOT_CHARGED_NOT_ASSISTANCE);
                return false;
            }
            int progress = Integer.parseInt(user.getProgress());
            if (progress < 30) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.NOT_CHARGED_START_DATES_ASSISTANT_BELLOW_30_PERCENT);
                return false;
            }
            return true;
        }
        LocalDate endDate = LocalDate.parse(user.getEndDate(), formatter);
        if (endDate.isBefore(LocalDate.now())) {
            if (user.getProgress() == null || user.getProgress().isBlank()) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.NOT_CHARGED_NOT_ASSISTANCE);
                return false;
            }
            int progress = Integer.parseInt(user.getProgress());
            if (progress < 80) {
                user.setState(Message.NOT_CHARGED);
                user.setDescription(Message.NOT_CHARGED_START_DATES_ASSISTANT_BELLOW_80_PERCENT);
                return false;
            }
        }
        return true;
    }

    private Optional<Boolean> userValidationLessThreeYears(UserDto user) {
        return userRepository.existsUser(
                user.getIdentification(),
                user.getStandardProgram(),
                LocalDate.now().minusYears(3));
    }


    private Map<String, UserEntity> saveUsers(List<UserDto> users) {
        List<RegionalEntity> regionalList=regionalService.getAllRegionals();
        List<UserEntity> entities = users.stream()
                .map(x -> {
                    UserEntity user=userMapper.userDtoToUserEntity(x);
                    CourseEntity course = courseService.findCourseByNames(x.getStandardProgram(), x.getMode().toUpperCase(),apiConfig.getYearSelected());
                    if(course!=null){
                        user.setCourse(course);
                        x.setProvider(course.getProvider().getName());
                    }

                    RegionalEntity regional = regionalService.findByName(regionalList,x.getRegional());
                    if(regional!=null){
                        user.setRegional(regional);
                    }
                    return user;
                })
                .toList();
        List<UserEntity> newEntities=userRepository.saveAll(entities);
        Map<String, UserEntity> map =newEntities.stream().collect(Collectors.toMap(UserEntity::getIdentificationNumber, x-> x));
        return map;
    }
    private List<UserEntity> updateStateUsers(List<UserDto> users){
        List<RegionalEntity> regionalList=regionalService.getAllRegionals();
        List<UserEntity> entities = users.stream()
                .map(x -> {
                    Optional<UserEntity> optUser=userRepository.findById(x.getUserId());
                    if(optUser.isPresent()){
                        UserEntity user = optUser.get();
                        user.setStatus(x.getState());
                        user.setDescription(x.getDescription());
                        userRepository.save(user);
                        return user;
                    }
                    return null;
                })
                .toList();
        return entities;
    }


    private ApiDocumentGenerationResponse sendUsersToKorlon(List<ApiInformationDatabase> users,String profile) {
        return externalServiceClient.fetchKorlonFromService(users,profile);
    }

    public ApiUserNotAceptableResponse findWithStatusNotAccepted(Long accountId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("chargeDate").descending());
        Page<UserEntity> userList= userRepository.findUserNotAccepted(accountId,pageable);
        return userMapper.userListToNotAcceptableResponse(userList);
    }

    public ApiCorrectUser correctUser(Long accountId,String identificationNumber,String newStatus) throws GenericException{
        Optional<UserEntity> optUser= userRepository.getUserByAccountId(accountId,identificationNumber);
        if(optUser.isPresent()){
            UserEntity user= optUser.get();
            String oldStatus=user.getStatus();
            String oldReason=user.getDescription();
            if(Objects.equals(user.getStatus(), Message.NOT_CHARGED)){
                user.setStatus(newStatus);
                user.setDescription(null);
                UserEntity newUser=userRepository.save(user);
                return userMapper.userToApiCorrectUser(newUser,oldStatus,oldReason);
            }else {
                throw new GenericException("Action not permitted");
            }
        }
        throw new GenericException("User does not exists");

    }

    public List<UserDto> validateUsersPending(Long accountId){
        List<UserDto> users = userRepository.getUsersByAccountId(accountId).stream()
                .filter(x-> x.getStatus().equals(Message.PENDING))
                .map(x-> userMapper.userEntityToUserDto(x))
                .toList();
        Flux<UserDto> fluxUsersToValidate = Flux.fromIterable(users);
        Flux<Tuple2<Boolean, UserDto>> validations = fluxUsersToValidate
                .groupBy(user -> {
                    return UserType.fromValue(user.getRegional());
                })
                .flatMap(groupedFlux -> {
                    return switch (groupedFlux.key()) {
                        //Usuarios tipo activo con regional valle
                        case ACTIVO -> groupedFlux.flatMap(user -> validateActiveUser(user)
                                .map(res -> Tuples.of(res, user))
                        );
                        //Usuarios con tipo cesante
                        case CESANTE -> validateLaidUsers(groupedFlux);
                        case DEFAULT,TALLERES -> Flux.empty();
                    };
                }, 100);
        Mono<Tuple2<List<UserDto>, List<UserDto>>> resultList = validations
                .collect(() -> Tuples.of(new ArrayList<UserDto>(), new ArrayList<UserDto>()), (tuple, entry) -> {
                    if (entry != null) {
                        boolean passed = entry.getT1();
                        if (passed) {
                            //cumplen las validaciones
                            tuple.getT1().add(entry.getT2());
                        } else {
                            //no cumplen las validaciones
                            tuple.getT2().add(entry.getT2());
                        }
                    }
                });
        Tuple2<List<UserDto>, List<UserDto>> userProcessingResultTuple = resultList.block();
        UserProcessingResult filterUsers= new UserProcessingResult(userProcessingResultTuple != null ? userProcessingResultTuple.getT1() : Collections.emptyList(),
                userProcessingResultTuple != null ? userProcessingResultTuple.getT2() : Collections.emptyList());

        List<UserDto> allUsers = new ArrayList<>(filterUsers.getUsersToValidate());
        allUsers.addAll(filterUsers.getExistingUsers());
        updateStateUsers(allUsers);
        return  allUsers;
    }

    public List<UserNotAcceptableResponse> updateUsersByFile(MultipartFile file, Long accountId,boolean revoke) throws IOException {
        return externalServiceClient.sendUpdateUsersByFile(file,accountId,revoke);
    }

    public List<UserDto> generateAccountDocuments(Long account,Boolean amortization){
        List<UserEntity> users= userRepository.getUsersByAccountId(account);
        if(users.isEmpty()){
            return Collections.emptyList();
        }
        UserType userType = UserType.fromValue(users.getFirst().getRegional().getName());
        List<UserDto> userDtoList= users.stream().map(x-> userMapper.userEntityToUserDto(x)).toList();
        List<ApiInformationDatabase> apiInformationDatabaseList= userDtoList.stream().map(x -> informationDatabaseMapper.userDtoToApiInformationDatabase(x)).toList();
        externalServiceClient.fetchKorlonGenerationFromService(apiInformationDatabaseList,UserType.validateProfile(userType),account,amortization);
        return  userDtoList;
    }
}
