package com.comfandi.phobos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.comfandi.phobos.entity.BillingApprovalEntity;
import com.comfandi.phobos.mapper.BillingAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.BillingAccountEntity;
import com.comfandi.phobos.repository.BillingAccountRepository;
import com.comfandi.phobos.service.dto.BillingAccountDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BillingAccountServices {

  @Autowired
  private BillingAccountMapper billingAccountMapper;

  @Autowired
  private final BillingAccountRepository billingAccountRepository;

  public List<BillingAccountDto> findAll() {
    List<BillingAccountEntity> billingAccounts = billingAccountRepository.findAll();
      return billingAccounts.stream()
          .map(
              billingAccount -> BillingAccountDto.builder()
                  .billingAccountId(billingAccount.getBillingAccountId())
                  .billingAccountType(billingAccount.getBillingAccountType())
                  .billingAccountName(billingAccount.getBillingAccountName())
                  .creationDate(billingAccount.getCreationDate())
                  .documentWordUrl(billingAccount.getDocumentWordUrl())
                  .documentExcelUrl(billingAccount.getDocumentExcelUrl())
                  .ley(billingAccount.getLaw())
                  .build())
          .toList();
  }

  public Long findByUserAndDate(Long userId, LocalDateTime date) {
      return billingAccountRepository.findBillingByUserAndDate(userId, date).orElse(0L);
  }

  public BillingAccountEntity getBillingAccount(Long id) {
    return  billingAccountRepository.findById(id).orElse(null);
  }

  public ResponseEntity<?> save(List<BillingAccountDto> billingAccounts) {
    List<BillingAccountEntity> billingAccountsEntities = billingAccounts.stream()
            .map(x->  billingAccountMapper.toEntity(x)).toList();
    billingAccountRepository.saveAll(billingAccountsEntities);
    return ResponseEntity.ok().build();
  }

  public List<BillingAccountEntity> getAllPendingAmortizableBillingAccounts(){
    return billingAccountRepository.findPendingAccountForAmortization();
  }

}
