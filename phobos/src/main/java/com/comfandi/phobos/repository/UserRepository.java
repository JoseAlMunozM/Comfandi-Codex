package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = " SELECT EXISTS ( " +
            " SELECT 1 FROM users u "+
            " JOIN course c ON u.id_course = c.id "+
            " WHERE u.identification_number = :identification "+
            " AND c.name = :nameCourse "+
            " AND u.training_date >= :startDate ) ", nativeQuery = true)
    Optional<Boolean> existsUser(@Param("identification") String identification,
                                 @Param("nameCourse") String program,
                                 @Param("startDate") LocalDate startDate);

    @Query("SELECT u FROM UserEntity u WHERE u.accountId=:accountId AND (u.status= 'NO COBRADO' OR u.status IS NULL)")
    public Page<UserEntity> findUserNotAccepted(Long accountId, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.accountId=:accountId AND  identificationNumber=:identificationNumber")
    Optional<UserEntity> getUserByAccountId(Long accountId, String identificationNumber);

    @Query("SELECT u FROM UserEntity u WHERE u.accountId=:accountId")
    List<UserEntity> getUsersByAccountId(Long accountId);

}
