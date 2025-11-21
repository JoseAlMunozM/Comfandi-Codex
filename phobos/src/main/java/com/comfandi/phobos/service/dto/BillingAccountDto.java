package com.comfandi.phobos.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingAccountDto {

  @JsonProperty("billingAccountId")
  private Integer billingAccountId;

  @JsonProperty("billing_account_type")
  private String billingAccountType;

  @JsonProperty("billing_account_name")
  private String billingAccountName;

  @JsonProperty("creation_date")
  private LocalDateTime creationDate;

  @JsonProperty("document_word_url")
  private String documentWordUrl;

  @JsonProperty("document_excel_url")
  private String documentExcelUrl;

  @JsonProperty("ley")
  private String ley;

  //todo: adicionar, status_global, status analista, status jefe,  numero de personas, total de la cuenta
}
