package com.comfandi.phobos.client;

import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.request.ApiLoginRequest;
import com.comfandi.phobos.client.request.ApiUserRequest;
import com.comfandi.phobos.client.response.*;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.dto.WorkshopUserDto;
import com.comfandi.phobos.service.enums.SourceType;
import com.comfandi.phobos.util.ApiConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ExternalWorkshopServiceClient {
    private final ApiConfig apiConfig;
    private final WebClient webClientComfandi;
    private final WebClient webClientJobs;
    private final WebClient webClientKorlon;

    public ExternalWorkshopServiceClient(WebClient.Builder webClientBuilder,ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.webClientComfandi = webClientBuilder.baseUrl(apiConfig.getUrlComfandi()).build();
        this.webClientJobs= webClientBuilder.baseUrl(apiConfig.getUrlJobs()).build();
        this.webClientKorlon = webClientBuilder.baseUrl(apiConfig.getUrlKorlon()).build();
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

    public List<WorkshopUserDto> fetchWorkshopUsersFromService(String token) {
        return fetchWorkshopUsersFromService(webClientComfandi,token);
    }

    private List<WorkshopUserDto> fetchWorkshopUsersFromService(WebClient webClient,String token) {
        ApiWorkshopUsersResponse response = webClient.get().
                uri(uriBuilder -> uriBuilder
                        .path(ApiConfig.URL_API_WORKSHOP_USERS)
                        .build())
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(token);
                })
                .retrieve()
                .bodyToMono(ApiWorkshopUsersResponse.class)
                .block();
        return response != null ? response.getData() : List.of();
    }
    public Mono<List<ApiWorkshopAppointmentsResponse>> fetchWorkshopAppointmentsFromService (List<ApiUserRequest> users, String token){
        return  fetchWorkshopAppointmentsFromService(webClientJobs,users,token);
    }

    private Mono<List<ApiWorkshopAppointmentsResponse>> fetchWorkshopAppointmentsFromService(WebClient webClient, List<ApiUserRequest> users, String token){
        return webClient.post()
                .uri(uriBuilder ->
                    uriBuilder.path(ApiConfig.URL_API_WORKSHOP_APPOINTMENT).build()

                ).headers(httpHeaders ->
                    httpHeaders.setBearerAuth(token))
                .bodyValue(users)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public ApiDocumentGenerationResponse fetchKorlonFromService(List<ApiInformationDatabase> users, String profile) {
        return fetchKorlonFromService(webClientKorlon, users, apiConfig.userKorlon, apiConfig.passwordKorlon,profile);
    }

    private ApiDocumentGenerationResponse fetchKorlonFromService(WebClient webClient, List<ApiInformationDatabase> users, String userKorlon, String passwordKorlon, String profile) {
        ApiDocumentGenerationResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiConfig.URL_KORLON_GENERATE_DOCUMENTS)
                        .queryParam("profile",profile)
                        .queryParam("source", SourceType.WORKSHOP.getValue()).build())
                .headers(headers -> headers.setBasicAuth(userKorlon, passwordKorlon))
                .bodyValue(users)
                .retrieve()
                .bodyToMono(ApiDocumentGenerationResponse.class)
                .block();

        return response;
    }
}
