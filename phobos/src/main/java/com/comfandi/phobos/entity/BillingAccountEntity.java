package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "billing_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "billing_account_id")
  private Integer billingAccountId;

  @Column(name = "billing_account_type", length = 50, nullable = false)
  private String billingAccountType;

  @Column(name = "billing_account_name", length = 100, nullable = false)
  private String billingAccountName;

  @Column(name = "creation_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime creationDate;

  @Column(name = "document_word_url", columnDefinition = "TEXT")
  private String documentWordUrl;

  @Column(name = "document_excel_url", columnDefinition = "TEXT")
  private String documentExcelUrl;

  @Column(name = "law" ,columnDefinition = "TEXT")
  private String law;

  @Column(name= "amortizable")
  private Boolean amortizable;

  @Column(name= "complete")
  private Boolean complete;
}
