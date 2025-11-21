package com.comfandi.korlon.repositories;
import com.comfandi.korlon.entities.BillingAccountEntity;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccountEntity, Long> {
}
