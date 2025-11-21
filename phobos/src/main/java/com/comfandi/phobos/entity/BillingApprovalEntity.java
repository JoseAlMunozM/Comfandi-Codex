package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_approvals")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingApprovalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "approval_id")
  private Integer approvalId;

  @Column(name = "billing_account_id", nullable = false)
  private Integer billingAccountId;

  @Column(name = "approval_date", nullable = false)
  private LocalDateTime approvalDate;

  @Column(name = "email_sent_date")
  private LocalDateTime emailSentDate;

  @Column(name = "identification_number")
  private Integer identificationNumber;

  @Column(name = "user_type")
  private String userType;

  @Column(name = "approval")
  private String approval;

  @Column(name = "observation")
  private String observation;

}
