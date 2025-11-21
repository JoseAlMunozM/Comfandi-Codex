package com.comfandi.phobos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentQuery {
private Long documentId;
    private LocalDate fechaCorte;
    private Integer version;
    private String estado;
    private String fileType;
    private String s3Url;
    private LocalDateTime fechaCreacion;
    private String usuarioCreador;
    private String aprobadoPor;
    private LocalDateTime fechaAprobacion;
}
