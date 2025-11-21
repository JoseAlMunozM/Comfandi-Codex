package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.UnavailableUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnavailableUserRepository extends JpaRepository<UnavailableUserEntity,Long> {

    @Query(value = "SELECT u FROM UnavailableUserEntity u WHERE unavailableDate BETWEEN :startDate AND :endDate")
    public List<UnavailableUserEntity> findByDates(LocalDate startDate, LocalDate endDate);
}
