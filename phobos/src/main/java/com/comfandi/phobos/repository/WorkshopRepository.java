package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.WorkshopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkshopRepository extends JpaRepository<WorkshopEntity,Long> {

    @Query(value = "SELECT w FROM WorkshopEntity w WHERE w.name=:name AND w.year=:year")
    Optional<WorkshopEntity> getByName(String name, Integer year);
}
