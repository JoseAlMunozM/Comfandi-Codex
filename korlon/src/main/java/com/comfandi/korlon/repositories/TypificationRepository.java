package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.TypificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypificationRepository extends JpaRepository<TypificationEntity,Long> {

    @Query(value = "SELECT t from TypificationEntity t WHERE t.type=:type")
    //@Query(value = "SELECT t.id, t.location, t.cebe, t.account, t.assignment, t.type from typification as t WHERE t.type=:type")
    List<TypificationEntity> findByType(String type);
}
