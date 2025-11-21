package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.WorkshopUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkshopUserRepository extends JpaRepository<WorkshopUserEntity,Long> {

    @Query(value = "SELECT ws FROM WorkshopUserEntity ws WHERE ws.identificationNumber=:identificationNumber " +
            "AND ws.workshop.id=:workshopId " +
            "AND ws.identificationType=:identificationType " +
            "AND ws.orientationDate>:valueDate")
    public List<WorkshopUserEntity> findRegistryBeforeDays(String identificationNumber, String identificationType, Long workshopId, LocalDate valueDate);
}
