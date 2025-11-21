package com.comfandi.phobos.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comfandi.phobos.entity.ValidationResult;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {

    @Query("""
        SELECT COUNT(v)
        FROM ValidationResult v
        WHERE v.estadofinal = 'inhabilitar'
        AND (:cutoffdate IS NULL OR v.cutoffdate <= :cutoffdate)
    """)
    long countDisabledUsers(@Param("cutoffdate") LocalDate cutoffdate);

    @Query("""
        SELECT v
        FROM ValidationResult v
        JOIN FETCH v.usuario u
        WHERE v.estadofinal = 'inhabilitar'
        AND (:cutoffdate IS NULL OR v.cutoffdate <= :cutoffdate)
        ORDER BY v.id DESC
    """)
    Page<ValidationResult> findDisabledUsers(@Param("cutoffdate") LocalDate cutoffdate, Pageable pageable);

    @Query("""
        SELECT v
        FROM ValidationResult v
        JOIN FETCH v.usuario u
        WHERE v.estadofinal = 'inhabilitar'
        AND (:cutoffdate IS NULL OR v.cutoffdate <= :cutoffdate)
        ORDER BY v.id DESC
    """)
    List<ValidationResult> findAllDisabledUsers(@Param("cutoffdate") LocalDate cutoffdate);


    @Query("""
        SELECT u.nombreCompleto, u.identificacion, u.programaEstandarizado, 
               v.estadofinal, v.observacion 
        FROM ValidationResult v 
        JOIN v.usuario u 
        WHERE v.cutoffdate = :cutoffdate
    """)
    List<Object[]> findResultadosConDatosUsuario(@Param("cutoffdate") LocalDate cutoffDate);
    
    @Modifying
    @Query("UPDATE ValidationResult v SET v.documentid = :documentid WHERE v.cutoffdate = :cutoffdate")
    void actualizarDocumentIdPorCutoffDate(@Param("cutoffdate") LocalDate cutoffdate, 
                                         @Param("documentid") Long documentid);
    
    boolean existsByCutoffdate(LocalDate cutoffdate);
    
    @Query("""
        SELECT v
        FROM ValidationResult v
        JOIN FETCH v.usuario u
        WHERE v.cutoffdate = :cutoffdate
        ORDER BY v.id DESC
    """)
    List<ValidationResult> findByCutoffdate(@Param("cutoffdate") LocalDate cutoffdate);

    @Query("SELECT u.identificacion, u.tipoIdentificacion, v.estadofinal " +
           "FROM ValidationResult v " +
           "JOIN v.usuario u " +
           "WHERE v.cutoffdate = :cutoffdate")
    List<Object[]> findDatosParaRAP(@Param("cutoffdate") LocalDate cutoffDate);
    
    @Query("SELECT u.identificacion, u.tipoIdentificacion " +
           "FROM ValidationResult v " +
           "JOIN v.usuario u " +
           "WHERE v.cutoffdate = :cutoffdate")
    List<Object[]> findTodosUsuariosCorte(@Param("cutoffdate") LocalDate cutoffDate);

    @Modifying
    @Query("UPDATE ValidationResult vr SET vr.estadofinal = 'validado', vr.fechavalidacion = CURRENT_DATE WHERE vr.cutoffdate = :cutoffdate AND vr.documentid = :documentid")
    int marcarUsuariosComoValidados(@Param("cutoffdate") LocalDate cutoffDate, @Param("documentid") Long documentId);
    
    @Query("SELECT COUNT(vr) FROM ValidationResult vr WHERE vr.cutoffdate = :cutoffdate AND vr.documentid = :documentid")
    long countUsuariosPorDocumento(@Param("cutoffdate") LocalDate cutoffDate, 
                                 @Param("documentid") Long documentId);


    @Modifying
    @Query("""
        UPDATE ValidationResult v
        SET v.estadofinal = :estadoFinal,
            v.observacion = :observacion
        WHERE v.usuario.tipoIdentificacion = :documentType
        AND v.usuario.identificacion = :documentNumber
    """)
    int updateEstadoFinalAndObservacion(
            @Param("documentType") String documentType,
            @Param("documentNumber") String documentNumber,
            @Param("estadoFinal") String estadoFinal,
            @Param("observacion") String observacion
    );

    @Modifying
    @Query("""
        UPDATE ValidationResult v
        SET v.fechavalidacion = CURRENT_DATE,
            v.estadofinal = 'validado'
        WHERE v.usuario.tipoIdentificacion = :documentType
        AND v.usuario.identificacion = :documentNumber
    """)
    int revalidateUser(
            @Param("documentType") String documentType,
            @Param("documentNumber") String documentNumber
    );

    @Modifying
    @Query("UPDATE ValidationResult v SET v.estadofinal = :estado WHERE v.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    @Query("""
        SELECT v.estadofinal
        FROM ValidationResult v
        WHERE v.usuario.tipoIdentificacion = :documentType
        AND v.usuario.identificacion = :documentNumber
        ORDER BY v.cutoffdate DESC, v.id DESC
    """)
    List<String> findEstados(
        @Param("documentType") String documentType,
        @Param("documentNumber") String documentNumber
    );

}