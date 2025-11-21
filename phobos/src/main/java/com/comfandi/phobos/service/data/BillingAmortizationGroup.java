package com.comfandi.phobos.service.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingAmortizationGroup {
    private String regional;
    private Long programId;
    private Long portfolioId;
    private Long modality;
}
