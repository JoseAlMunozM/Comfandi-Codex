package com.comfandi.phobos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.comfandi.phobos.entity.BillingAccountAmortizationEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingAccountAmortizationRepository extends JpaRepository<BillingAccountAmortizationEntity,Long> {
    @Query(value = "SELECT ae FROM BillingAccountAmortizationEntity ae " +
            "WHERE ae.billingAccount.billingAccountId=:account " +
            "AND ae.course.id=:programId " +
            "AND ae.portfolio.id=:portfolioId "+
            "AND ae.amortizationDate<:amortizationLimitDate")
    List<BillingAccountAmortizationEntity> findByAccountId(Long account,Long programId,Long portfolioId, LocalDate amortizationLimitDate);

    @Query(value = "SELECT ae FROM BillingAccountAmortizationEntity ae " +
            " WHERE ae.billingAccount.billingAccountId=:account " +
            " AND ae.course.id=:programId " +
            " AND ae.portfolio.id=:portfolioId " +
            " AND ae.amortizationNumber=:amortizationNumber")
    public Optional<BillingAccountAmortizationEntity> findAmortization(Long account, Long programId,
                                                                 Long portfolioId, Integer amortizationNumber,Double value);
}
