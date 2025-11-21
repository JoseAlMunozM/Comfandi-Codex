package com.comfandi.korlon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.comfandi.korlon.api.response.ApiBillingAccountResponse;
import com.comfandi.korlon.services.PendingsService;

import java.util.*;


@RestController
@RequestMapping("/api/v1")
public class ApiPendings {


    @Autowired
	private PendingsService pendingsService;

	@GetMapping("/pending-collections")
    public ResponseEntity<List<ApiBillingAccountResponse>> getEmpresasPendientesCobro() {
        List<ApiBillingAccountResponse> response = pendingsService.obtenerEmpresasPendientesCobro()
                .stream()
                .map(account -> new ApiBillingAccountResponse(
                         account.getBillingAccountId(),
						account.getBillingAccountName(),
						account.getBillingAccountType(),
						account.getLaw(),
						account.getDocumentWordUrl(),
						account.getDocumentExcelUrl(),
						account.getAmortizable(),
						account.getComplete()))
                .toList();

        return ResponseEntity.ok(response);
    }

}
