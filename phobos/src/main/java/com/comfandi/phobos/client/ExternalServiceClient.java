package com.comfandi.phobos.client;

import com.comfandi.phobos.client.request.*;
import com.comfandi.phobos.client.response.*;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.enums.SourceType;
import com.comfandi.phobos.util.ApiConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalServiceClient {


    private final WebClient webClientComfandi;
    private final WebClient webClientKorlon;
    private final WebClient webClientFomento;
    private final WebClient webClientMpac;
    private final WebClient webClientPostulation;


    private final ApiConfig apiConfig;

    public ExternalServiceClient(WebClient.Builder webClientBuilder, ApiConfig apiConfig) {
    	System.out.println("Service URL: --------------------------------------------");
    	    	System.out.println("Service URL: >" + apiConfig.getUrlComfandi() +"<");
    	    	System.out.println("Service URL: >" + apiConfig.getUrlKorlon()+"<");
    	    	System.out.println("Service URL: >" + apiConfig.getUrlFomento()+"<");
    	    	System.out.println("Service URL: >" + apiConfig.getUrlPersonas()+"<");
    	    	System.out.println("Service URL: >" + apiConfig.getUrlPostulation()+"<");
    	    	System.out.println("Service URL: --------------------------------------------");
    	 
        this.apiConfig = apiConfig;
        this.webClientComfandi = webClientBuilder.baseUrl(apiConfig.getUrlComfandi()).build();
        this.webClientKorlon = webClientBuilder.baseUrl(apiConfig.getUrlKorlon()).build();
        this.webClientFomento =  webClientBuilder.baseUrl(apiConfig.getUrlFomento()).build();
        this.webClientMpac= webClientBuilder.baseUrl(apiConfig.getUrlPersonas()).build();
        this.webClientPostulation= webClientBuilder.baseUrl(apiConfig.getUrlPostulation()).build();
    }

    /**
     * Consume el servicio 1 y obtiene la lista de usuarios dentro de "data".
     */
    public List<UserDto> fetchUsuariosFromService(String token) {
        return fetchUsuariosFromService(webClientComfandi,token);
    }


    public ApiDocumentGenerationResponse fetchKorlonFromService(List<ApiInformationDatabase> users, String profile) {
        return fetchKorlonFromService(webClientKorlon, users, apiConfig.userKorlon, apiConfig.passwordKorlon,profile);
    }

    /**
     * Método genérico para consumir servicios que retornan una estructura similar
     * con un array "data".
     */
    private List<UserDto> fetchUsuariosFromService(WebClient webClient,String token) {
        Map<String, Object> body = Map.of(
            "parametros_consulta", Map.of(
                "fecha_inicio", "2025-10-19",
                "fecha_fin", "2025-11-19",
                "estado_inscripcion", ""
            )
        );

        ApiResponse response = webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(ApiConfig.URL_API_USERS)
                .build())
            .headers(h -> h.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(ApiResponse.class)
            .block();

        System.out.println("-- Lista de usuarios ------------------------- " + response);
        return response != null ? response.getData() : List.of();
    }


    private ApiDocumentNotificationResponse fetchKorlonEmailNotification(WebClient webClient,
                                                                         ApiEmailNotificationRequest request,String userKorlon,String passwordKorlon) {

        return webClient.post().uri(ApiConfig.URL_KORLON_EMAIL_NOTIFICATIONS)
                .headers(headers -> headers.setBasicAuth(userKorlon, passwordKorlon))
                        .bodyValue(request)
                .exchangeToMono(res->  res.bodyToMono(ApiDocumentNotificationResponse.class)
                ).block();
    }

    public ApiDocumentNotificationResponse fetchKorlonEmailNotificationFromService(ApiEmailNotificationRequest request) {
        return fetchKorlonEmailNotification(webClientKorlon, request, apiConfig.userKorlon, apiConfig.passwordKorlon);
    }

    private ApiDocumentGenerationResponse fetchKorlonFromService(WebClient webClient, List<ApiInformationDatabase> users,String userKorlon,String passwordKorlon,String profile) {
        ApiDocumentGenerationResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiConfig.URL_KORLON_GENERATE_DOCUMENTS)
                        .queryParam("profile",profile)
                        .queryParam("source", SourceType.COURSES.getValue()).build())
                .headers(headers -> headers.setBasicAuth(userKorlon, passwordKorlon))
                .bodyValue(users)
                .retrieve()
                .bodyToMono(ApiDocumentGenerationResponse.class)
                .block();

        return response;
    }

    public ApiDocumentGenerationResponse fetchKorlonAmortizeromService(List<ApiInformationDatabase> users, String profile,Long account) {
        return fetchKorlonAmortizeFromService(webClientKorlon, users, apiConfig.userKorlon, apiConfig.passwordKorlon,profile,account);
    }

    public ApiDocumentGenerationResponse fetchKorlonGenerationFromService(List<ApiInformationDatabase> users, String profile,Long account,Boolean amortization) {
        return fetchKorlonGenerationFromService(webClientKorlon, users, apiConfig.userKorlon, apiConfig.passwordKorlon,profile,account,amortization);
    }



    private ApiDocumentGenerationResponse fetchKorlonAmortizeFromService(WebClient webClient, List<ApiInformationDatabase> users,String userKorlon,String passwordKorlon,String profile, Long account) {
        ApiDocumentGenerationResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiConfig.URL_KORLON_AMORTIZE_DOCUMENTS)
                        .queryParam("profile",profile)
                        .queryParam("account",account)
                        .queryParam("source", SourceType.COURSES.getValue())
                        .build())
                .headers(headers -> headers.setBasicAuth(userKorlon, passwordKorlon))
                .bodyValue(users)
                .retrieve()
                .bodyToMono(ApiDocumentGenerationResponse.class)
                .block();

        return response;
    }

    private ApiDocumentGenerationResponse fetchKorlonGenerationFromService(WebClient webClient, List<ApiInformationDatabase> users,String userKorlon,String passwordKorlon,String profile, Long account,Boolean amortization) {
        ApiDocumentGenerationResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiConfig.URL_KORLON_RE_GENERATE_DOCUMENTS)
                        .queryParam("profile",profile)
                        .queryParam("account",account)
                        .queryParam("source", SourceType.COURSES.getValue())
                        .queryParam("amortization",amortization).build())
                .headers(headers -> headers.setBasicAuth(userKorlon, passwordKorlon))
                .bodyValue(users)
                .retrieve()
                .bodyToMono(ApiDocumentGenerationResponse.class)
                .block();

        return response;
    }

    public ApiLoginResponse loginPostulationFromService(String user, String password) {
        return loginPostulation(user,password);
    }

    private ApiLoginResponse loginPostulation(String user, String password) {
        return webClientPostulation.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiConfig.getURL_API_SUBSIDIO_USER_STATUS_LOGIN())
                        .build())
                .bodyValue(new ApiLoginRequest(user,password))
                .retrieve()
                .bodyToMono(ApiLoginResponse.class).block();
    }

    public Mono<List<ApiUserStateResponse>> fetchUserStateFromService(List<ApiUserRequest> users,String token) {
        return fetchUsersStateService(webClientPostulation, users,token);
    }

    private Mono<List<ApiUserStateResponse>> fetchUsersStateService(WebClient webClient, List<ApiUserRequest> users,String token) {
        ApiPostulationUserRequest request=new ApiPostulationUserRequest(users);
        return webClient.post()
                .uri( uriBuilder ->
                        uriBuilder.path(apiConfig.URL_API_SUBSIDIO_USER_STATUS)
                                .build())
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(token);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public Mono<List<ApiUserResidenceResponse>> fetchUsersResidenceFromService(List<ApiUserRequest> users) {
        return fetchUsersResidenceService(webClientFomento, users);
    }

    private Mono<List<ApiUserResidenceResponse>> fetchUsersResidenceService(WebClient webClient, List<ApiUserRequest> users) {

        return webClient.post()
                .uri(apiConfig.URL_API_FOMENTO_GET_USER_RESIDENCE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(users)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public ApiLoginResponse loginFromService(String user, String password) {
        return login(user,password);
    }

    private ApiLoginResponse login(String user, String password) {
        return webClientComfandi.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(ApiConfig.URL_API_LOGIN)
                                .build())
                .bodyValue(new ApiLoginRequest(user,password))
                        .retrieve()
                .bodyToMono(ApiLoginResponse.class).block();
    }

    public Mono<List<ApiUnavailableUsersResponse>> fetchUnavailableFromService(String token){
        return fetchUnavailableUsersService(webClientComfandi,token);
    }

    private Mono<List<ApiUnavailableUsersResponse>> fetchUnavailableUsersService(WebClient webClient,String token){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(apiConfig.URL_API_UNAVAILABLE_USERS).build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public Mono<List<ApiUnavailableUserApprovalResponse>> fetchUnavailableApprovalFromService(List<ApiUserRequest> unavailableUser,String token){
        return fetchUnavailableUsersApprovalService(webClientComfandi,unavailableUser,token);
    }

    public Mono<List<ApiUnavailableUserApprovalResponse>> fetchUnavailableUsersApprovalService(WebClient webClient,List<ApiUserRequest> unavailableUser,String token){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiConfig.URL_API_UNAVAILABLE_USERS_APPROVAL).build())
                .bodyValue(unavailableUser)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public Mono<List<ApiUnavailableUserStatusResponse>> fetchUnavailableStatusFromService(List<ApiUserRequest> unavailableUser,String token){
        return fetchUnavailableUsersStatusService(webClientComfandi,unavailableUser,token);
    }

    public Mono<List<ApiUnavailableUserStatusResponse>> fetchUnavailableUsersStatusService(WebClient webClient,List<ApiUserRequest> unavailableUser,String token){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiConfig.URL_API_UNAVAILABLE_USERS_STATUS).build())
                .bodyValue(unavailableUser)
                .exchangeToMono(res -> res.bodyToMono(new ParameterizedTypeReference<>() {
                }));
    }

    public  String fetchUnavailableNotificationFromService(List<ApiInformationDatabase> data,String email){
        return fetchUnavailableNotificationService(webClientKorlon, apiConfig.userKorlon,apiConfig.passwordKorlon,data,email);
    }

    private String fetchUnavailableNotificationService (WebClient webClient, String userKorlon,String passwordKorlon, List<ApiInformationDatabase> data,String emailNotification){
        return webClient.post().uri(
                uriBuilder -> uriBuilder.path(ApiConfig.ULR_KORLON_UNAVAILABLE_NOTIFICATIONS).build()
                )
                .bodyValue(ApiInformationDatabaseEmailRequest.builder().email(emailNotification).data(data).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public  String fetchRevokedNotificationFromService(List<ApiInformationDatabase> data,String email){
        return fetchRevokedNotificationService(webClientKorlon, apiConfig.userKorlon,apiConfig.passwordKorlon,data,email);
    }

    private String fetchRevokedNotificationService (WebClient webClient, String userKorlon,String passwordKorlon, List<ApiInformationDatabase> data,String emailNotification){
        return webClient.post().uri(
                        uriBuilder -> uriBuilder.path(ApiConfig.ULR_KORLON_REVOKED_USERS_NOTIFICATIONS).build()
                )
                .bodyValue(ApiInformationDatabaseEmailRequest.builder().email(emailNotification).data(data).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    public List<UserNotAcceptableResponse> sendUpdateUsersByFile( MultipartFile file, Long accountId,boolean revoke) throws IOException {
        return fetchSendUpdateUsersByFile(webClientKorlon, file ,accountId,revoke);
    }

    private List<UserNotAcceptableResponse> fetchSendUpdateUsersByFile(WebClient webClient, MultipartFile file, Long accountId,boolean revoke) throws IOException {

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // Muy importante
            }
        };
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new HttpEntity<>(resource, createHeadersForFile(file)));

        return webClient.post().uri(
                uriBuilder -> uriBuilder.path(revoke?ApiConfig.ULR_KORLON_REVOKED_USERS_FILE:ApiConfig.ULR_KORLON_UPDATE_USERS_FILE)
                        .queryParam("accountId",accountId.toString())
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserNotAcceptableResponse>>() {
                }).block();
    }
    private HttpHeaders createHeadersForFile(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("file", file.getOriginalFilename());
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        return headers;
    }

    
    //  CHECAR CON ANDRES (  demasiada concurrencia satura Hilos , validar ) replantear PEND
    public Mono<List<ApiBusinessUserResponse>> fetchBusinessIdentifierFromService(List<ApiUserRequest> users,String apikey){
        return fetchBusinessIdentifier(webClientMpac,users,apikey);
    }

    
    //VALIDAR ESE MONO
    private Mono<List<ApiBusinessUserResponse>> fetchBusinessIdentifier(WebClient webClient,List<ApiUserRequest> users,String apikey){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiConfig.URL_API_BUSINESS_IDENTIFIER).build())
                .headers(httpHeaders -> httpHeaders.add("x-api-key",apikey))
                .bodyValue(users)
                .exchangeToMono(res -> res.bodyToMono(new ParameterizedTypeReference<>() {
                }));
    }
}