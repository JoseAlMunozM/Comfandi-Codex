package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.WorkshopUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkshopUserRepository extends JpaRepository<WorkshopUserEntity,Long> {

    @Modifying
    @Query("UPDATE WorkshopUserEntity u SET u.accountId = :accountId, validationDate=:chargeDate WHERE u.id = :userId")
    public void  updateIdAccounts(Long userId, Long accountId, LocalDate chargeDate);
}
