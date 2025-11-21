package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.UserValidationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface LastUserLoginRepository extends JpaRepository<UserValidationHistoryEntity, String> {

    @Query("""
                SELECT u.createdAt FROM UserValidationHistoryEntity u
                WHERE u.identificationNumber = :identificationNumber
                ORDER BY u.createdAt DESC
                LIMIT 1
            """)
    Date lastUserLogin(
            @Param("identificationNumber") String identificationNumber);
}