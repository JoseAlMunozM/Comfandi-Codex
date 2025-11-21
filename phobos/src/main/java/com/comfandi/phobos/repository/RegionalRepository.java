package com.comfandi.phobos.repository;

import com.comfandi.phobos.entity.RegionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionalRepository extends JpaRepository<RegionalEntity,Long> {
}
