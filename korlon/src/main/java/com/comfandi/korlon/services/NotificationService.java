package com.comfandi.korlon.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.comfandi.korlon.manager.data.UnavailableUsers;
import com.comfandi.korlon.utils.aws.AwsSESmanager;

@Service
public class NotificationService {
    
    @Value("${api.phobos-user-name}")
    private String phobosUser;

    @Value("${api.phobos-user-password}")
    private String phobosPass;

    @Autowired
    private WebClient webClient;

    @Autowired
    private AwsSESmanager awsSESmanager;

    public void notificarUsuariosInhabilitados(LocalDate startDate, LocalDate endDate) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convertir las fechas al formato correcto
        String start = startDate.format(formatter);
        String end   = endDate.format(formatter);

        List<UnavailableUsers> usuarios = Arrays.asList(
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                        .path("unavailable-users/find-users")
                        .queryParam("startDate", start)
                        .queryParam("endDate", end)
                        .build()
                )
                .headers(headers -> headers.setBasicAuth(phobosUser, phobosPass))
                .retrieve()
                .bodyToMono(UnavailableUsers[].class)
                .block()
        );

        for (UnavailableUsers usuario : usuarios) {
             /*usuario.setEmail("jessicapantoja@comfandi.com.co");
              */
            try {
                if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {

                    awsSESmanager.sendEmail(
                            "no-reply@comfandi.com.co",
                            usuario.getEmail(),
                            "Notificación de inhabilitación",
                            String.format("Hola %s, su cuenta fue inhabilitada el %s.\nCausa: %s",
                                    usuario.getNombreCompleto(),
                                    usuario.getFecInhabilitacion(),
                                    usuario.getCausa())
                    );
                    usuario.setNotificacion(true);

                    /*webClient.patch()
                            .uri("/usuarios/{id}", usuario.getUserId())
                            .body(Mono.just(usuario), UnavailableUsers.class)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();
                    */
                } else {
                    System.out.println("El usuario " + usuario.getNombreCompleto() + " no tiene correo registrado.");
                }

            } catch (Exception e) {
                System.err.println("Error notificando a " + usuario.getNombreCompleto() + ": " + e.getMessage());
            }
        }
    }
}
