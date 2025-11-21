package com.comfandi.phobos.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class ApiConfig {

    @Value("${api.comfandi_baseurl}")
    public String urlComfandi;

    @Value("${api.comfandi_user}")
    public String userComfandi;

    @Value("${api.comfandi_pass}")
    public String passComfandi;

    @Value("${api.subsidio_user}")
    public String userSubsidioComfandi;

    @Value("${api.subsidio_pass}")
    public String passSubsidioComfandi;

    @Value("${api.korlon_baseurl}")
    public String urlKorlon;

    @Value("${api.fomento_baseurl}")
    public String urlFomento;
    @Value("${api.postulation_baseurl}")
    public String urlPostulation;

    @Value("${api.jobs_baseurl}")
    public String urlJobs;

    @Value("${api.personas_baseurl}")
    public String urlPersonas;
    @Value("${api.personas_apikey}")
    public String apikeyPersonas;

    //** KORLON URLS ***//
    public static final String URL_KORLON_GENERATE_DOCUMENTS = "generate-documents";
    public static final String URL_KORLON_RE_GENERATE_DOCUMENTS = "regenerate-documents";
    public static final String URL_KORLON_AMORTIZE_DOCUMENTS = "amortize-documents";
    public static final String URL_KORLON_EMAIL_NOTIFICATIONS = "send-documents";
    public static final String ULR_KORLON_UNAVAILABLE_NOTIFICATIONS ="unavailable-users/send-documents";
    public static final String ULR_KORLON_REVOKED_USERS_NOTIFICATIONS= "send-documents/revoked";
    public static final String ULR_KORLON_REVOKED_USERS_FILE= "send-documents/revoked-users-file";
    public static final String ULR_KORLON_UPDATE_USERS_FILE= "send-documents/update-users-file";

    @Value("${api.year}")
    public Integer yearSelected=2025;

    @Value("${api.korlon-user-name}")
    public String userKorlon;
    @Value("${api.korlon-user-password}")
    public String passwordKorlon;

    @Value("${api.file_email_notification}")
    public String emailNotification;
    @Value("${api.cors_allowed_origins}")
    public String [] allowedOrigins;

    //API
    public static final String URL_API_LOGIN = "/comfandi/app_api/login";
    public static final String URL_API_USERS = "/comfandi/app_api/get";
    public static final String URL_API_WORKSHOP_USERS = "/comfandi/app_api/get_workshop_users";
    public String URL_API_UNAVAILABLE_USERS = "comfandi/app_api/get_unavailable_users";

    public static final String URL_API_WORKSHOP_APPOINTMENT="/workshop/appointments";
    public String URL_API_SUBSIDIO_USER_STATUS_LOGIN;
    public String URL_API_SUBSIDIO_USER_STATUS;
    public String URL_API_FOMENTO_GET_USER_RESIDENCE;
    public String URL_API_UNAVAILABLE_USERS_APPROVAL;
    public String URL_API_UNAVAILABLE_USERS_STATUS;
    public String URL_API_BUSINESS_IDENTIFIER;


    @PostConstruct
    public void init() {
        URL_API_SUBSIDIO_USER_STATUS_LOGIN= "/api/v1/users/login/himalia/";
        URL_API_SUBSIDIO_USER_STATUS = "/api/v1/postulations/postulation-state-jobmentor";
        URL_API_FOMENTO_GET_USER_RESIDENCE ="/unemployed/document-register";
        URL_API_BUSINESS_IDENTIFIER = "/user-mpac/query-information";
        URL_API_UNAVAILABLE_USERS_APPROVAL = "/unemployed/user-approval";
        URL_API_UNAVAILABLE_USERS_STATUS = "/unemployed/user-status";
    }

}
