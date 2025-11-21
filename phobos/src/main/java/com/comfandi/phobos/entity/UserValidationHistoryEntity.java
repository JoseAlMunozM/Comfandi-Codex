package com.comfandi.phobos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_validation_history")
public class UserValidationHistoryEntity {

  @Id
  @Column(nullable = false)
  private String id;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Date createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;

  @Column(name = "identification_number", nullable = false)
  private String identificationNumber;

  @Column(name = "identification_type", nullable = false)
  private String identificationType;

  @Column(name = "validation_pass", nullable = false)
  private boolean validationPass;

  @Column(name = "response_data", nullable = false, columnDefinition = "json")
  private String responseData;

  @Column(name = "law")
  private String law;

  @Column(name = "business_name")
  private String businessName;

  @Column(name = "business_identification")
  private String businessIdentification;

  @Column(name = "state")
  private boolean state;
}
