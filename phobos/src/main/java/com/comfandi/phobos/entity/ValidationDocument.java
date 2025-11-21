package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documentos", schema = "fomento")
public class ValidationDocument {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documentid")
    private Long documentId;
    
    @Column(name = "cutoffdate", nullable = false)
    private LocalDate cutoffDate;
    
    @Column(name = "filetype")
    private String fileType;
    
    @Column(name = "s3url", nullable = false)
    private String s3Url;
    
    @Column(name = "createdby", nullable = false)
    private String createdBy;
    
    @Column(name = "createdat")
    private LocalDateTime createdAt;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "estadodocumento")
    private String estadoDocumento;

    @Column(name = "approvedby")
    private String approvedBy;
    
    @Column(name = "approvedat")
    private LocalDateTime approvedAt;
    
}
