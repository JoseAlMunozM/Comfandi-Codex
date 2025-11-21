package com.comfandi.phobos.controller;

import com.comfandi.phobos.service.BillingAccountServices;
import com.comfandi.phobos.service.BillingApprovalService;
import com.comfandi.phobos.service.dto.BillingAccountDto;
import com.comfandi.phobos.service.dto.UpdateBillingApprovalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/billing")
public class ApiBillingController {
    @Autowired
    private BillingApprovalService updateBillingApprovalService;

    @Autowired
    private BillingAccountServices billingAccountServices;

    @PostMapping("/billing-approval")
    public ResponseEntity<?> updateBillingApproval(@RequestBody UpdateBillingApprovalDto billingApprovalDto) throws Exception {
        return  updateBillingApprovalService.updateBillingApproval(billingApprovalDto);
    }

    @GetMapping("/billing-account")
    public ResponseEntity<List<BillingAccountDto>> findAllBillingAccount() {
        List<BillingAccountDto> billingAccounts = billingAccountServices.findAll();
        return ResponseEntity.ok(billingAccounts);
    }
}
