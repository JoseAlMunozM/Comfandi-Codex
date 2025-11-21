package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.ValidationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface  ValidationDocumentRepository extends JpaRepository<ValidationDocument, Long> {
    
    Optional<ValidationDocument> findTopByCutoffDateAndFileTypeOrderByVersionDesc(
        LocalDate cutoffDate, String fileType);
    
    Optional<ValidationDocument> findByDocumentId(Long documentId);
    
    List<ValidationDocument> findByCutoffDate(LocalDate cutoffDate);
    
    List<ValidationDocument> findByFileType(String fileType);

    @Query("SELECT vd FROM ValidationDocument vd WHERE vd.cutoffDate = :cutoffDate AND vd.fileType IN ('RAP01', 'RAP02')")
    List<ValidationDocument> findRapDocumentsByCutoffDate(@Param("cutoffDate") LocalDate cutoffDate);


    List<ValidationDocument> findByCutoffDateOrderByCreatedAtDesc(LocalDate cutoffDate);
    
    @Query("SELECT vd FROM ValidationDocument vd ORDER BY vd.cutoffDate DESC, vd.createdAt DESC")
    List<ValidationDocument> findAllByOrderByCutoffDateDescCreatedAtDesc();
    
    @Query("SELECT vd.cutoffDate, vd.fileType, COUNT(vd), MAX(vd.version), MAX(vd.createdAt) " +
           "FROM ValidationDocument vd " +
           "GROUP BY vd.cutoffDate, vd.fileType " +
           "ORDER BY vd.cutoffDate DESC")
    List<Object[]> findResumenDocumentosPorCorte();
    
    @Query("SELECT vd FROM ValidationDocument vd WHERE vd.cutoffDate BETWEEN :fechaInicio AND :fechaFin ORDER BY vd.cutoffDate DESC, vd.createdAt DESC")
    List<ValidationDocument> findByCutoffDateBetween(@Param("fechaInicio") LocalDate fechaInicio, 
                                                   @Param("fechaFin") LocalDate fechaFin);

    Optional<ValidationDocument> findFirstByFileTypeOrderByCreatedAtDesc(String fileType);


}