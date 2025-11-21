package com.comfandi.korlon.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.comfandi.korlon.entities.BillingAccountEntity;


@Repository
public interface PendingRepository extends JpaRepository<BillingAccountEntity, Long> {
    List<BillingAccountEntity> findByCompleteFalse();
}
