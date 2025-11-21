package com.comfandi.phobos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comfandi.phobos.entity.BillingAccountEntity;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillingAccountRepository extends JpaRepository<BillingAccountEntity, Long> {


    @Query(value = "SELECT COUNT(1) FROM billing_accounts ba " +
            "WHERE ba.user_id= :userId AND ba.creation_date >= :date", nativeQuery = true)
    public Optional<Long> findBillingByUserAndDate(Long userId, LocalDateTime date);

    @Query(value = "SELECT ba FROM BillingAccountEntity ba WHERE ba.amortizable = true AND ba.complete=false")
    public List<BillingAccountEntity> findPendingAccountForAmortization();
}
