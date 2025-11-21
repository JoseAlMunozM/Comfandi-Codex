package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.CourseValidated;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseValidatedRepository extends JpaRepository<CourseValidated, Long> {
    
    CourseValidated findByIdentificacion(String identificacion);
    
    boolean existsByIdentificacion(String identificacion);

    Optional<CourseValidated> findByTipoIdentificacionAndIdentificacion(
            String tipoIdentificacion,
            String identificacion
    );
}