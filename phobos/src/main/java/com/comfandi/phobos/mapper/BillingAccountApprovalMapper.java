package com.comfandi.phobos.mapper;

import com.comfandi.phobos.client.response.BillingApprovalResponse;
import com.comfandi.phobos.entity.BillingApprovalEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class BillingAccountApprovalMapper {
    public BillingApprovalResponse billingApprovalEntityToBillingApprovalResponse(BillingApprovalEntity billingApprovalEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BillingApprovalResponse billingApprovalResponse = new BillingApprovalResponse();
        billingApprovalResponse.setBillingAccountId(billingApprovalEntity.getBillingAccountId().toString());
        billingApprovalResponse.setApproval(billingApprovalEntity.getApproval());
        billingApprovalResponse.setApprovalId(billingApprovalEntity.getApprovalId().longValue());
        billingApprovalResponse.setType(billingApprovalEntity.getUserType());
        billingApprovalResponse.setObservations(billingApprovalEntity.getObservation());
        billingApprovalResponse.setApprovalDate(billingApprovalEntity.getApprovalDate().format(formatter));
        return billingApprovalResponse;
    }
}
