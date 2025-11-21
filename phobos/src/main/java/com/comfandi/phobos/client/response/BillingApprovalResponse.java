package com.comfandi.phobos.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingApprovalResponse {
    @JsonProperty("approval_id")
    private Long approvalId;

    @JsonProperty("account_id")
    private String billingAccountId;

    @JsonProperty("approval_date")
    private String approvalDate;

    @JsonProperty("approval_type")
    private String type;

    @JsonProperty("approval")
    private String approval;

    @JsonProperty("approval_observation")
    private String observations;

    @JsonProperty("notification_sent")
    private String emailSent;

}
