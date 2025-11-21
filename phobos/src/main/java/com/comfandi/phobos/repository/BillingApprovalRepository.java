package com.comfandi.phobos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comfandi.phobos.entity.BillingApprovalEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BillingApprovalRepository extends JpaRepository<BillingApprovalEntity, Long> {

    @Query(value = "select u from BillingApprovalEntity u " +
            "where u.billingAccountId=:billingId " +
            "and u.userType=:userType")
    public Optional<BillingApprovalEntity> findBillingApprovalByidAccount(Long billingId,String userType);


}
