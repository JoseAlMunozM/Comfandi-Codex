package com.comfandi.phobos.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.comfandi.phobos.entity.ValidationResult;
import com.comfandi.phobos.service.ValidationResultService;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/api/v1/fomento-docs")
public class ApiDisabledUserController {
 @Autowired
    private ValidationResultService service;

    @GetMapping("/disabled")
    public ResponseEntity<Object> getDisabledUsers(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate cutoffDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        try {
            // Paso 1: contar cuántos usuarios hay
            long total = service.countDisabledUsers(cutoffDate);

            if (total == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", 404,
                        "error", "No data found",
                        "message", "No users found with status 'inhabilitar'."
                ));
            }

            // Paso 2: decidir si se pagina o no
            boolean shouldPaginate = total > 1000;

            List<Map<String,Object>> users;
            Map<String, Object> response = new LinkedHashMap<>();

            if (shouldPaginate) {
                int pageSize = (size != null && size > 0 && size <= 1000) ? size : 1000;
                Pageable pageable = PageRequest.of(page, pageSize);

                Page<ValidationResult> pagedResults = service.getDisabledUsers(cutoffDate, pageable);

               users = pagedResults.getContent().stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("identification", r.getUsuario().getIdentificacion());
                    map.put("program", r.getPrograma());
                    map.put("finalStatus", r.getEstadofinal());
                    map.put("observation", r.getObservacion());
                    return map;
                })
                .toList();


                response.put("paginated", true);
                response.put("page", page);
                response.put("size", pageSize);
                response.put("total", total);
                response.put("pages", pagedResults.getTotalPages());
                response.put("users", users);
            } else {
                // Si ≤1000, devolvemos todos
                users = service.getAllDisabledUsers(cutoffDate).stream()
                    .map(r -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("identification", r.getUsuario().getIdentificacion());
                        map.put("program", r.getPrograma());
                        map.put("finalStatus", r.getEstadofinal());
                        map.put("observation", r.getObservacion());
                        return map;
                    })
                    .toList();
                response.put("paginated", false);
                response.put("total", total);
                response.put("users", users);
            }

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", ex.getMessage()
            ));
        }
    }
}