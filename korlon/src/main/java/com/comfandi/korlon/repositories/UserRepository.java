package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Modifying
    @Query("UPDATE UserEntity u SET u.accountId = :accountId, chargeDate=:chargeDate WHERE u.id = :userId")
    public void  updateIdAccounts(Long userId, Long accountId, LocalDate chargeDate);

    @Query("SELECT u FROM UserEntity u WHERE u.accountId=:accountId AND  identificationNumber=:identificationNumber")
    Optional<UserEntity> getUserByAccountId(Long accountId, String identificationNumber);


    @Query("SELECT u FROM UserEntity u WHERE u.accountId=:accountId")
    List<UserEntity> getUsersByAccountId(Long accountId);
}
