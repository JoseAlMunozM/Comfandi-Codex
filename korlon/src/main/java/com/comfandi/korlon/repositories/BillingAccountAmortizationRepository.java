package com.comfandi.korlon.repositories;


import com.comfandi.korlon.entities.BillingAccountAmortizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillingAccountAmortizationRepository extends JpaRepository<BillingAccountAmortizationEntity,Long> {
    @Query(value = "SELECT ae FROM BillingAccountAmortizationEntity ae WHERE ae.billingAccount.id=:accountId")
    //@Query(value = "SELECT ae.id, ae.billing_account_id, ae.amortization_date, ae.year FROM billing_account_amortization as ae WHERE ae.id=:accountId")
    List<BillingAccountAmortizationEntity> findByAccountId(Long accountId);

}
