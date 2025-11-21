package com.comfandi.phobos.controller;


import com.comfandi.phobos.entity.BillingAccountAmortizationEntity;
import com.comfandi.phobos.service.BillingAccountAmortizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/amortization")
public class ApiAmortizationController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private BillingAccountAmortizationService billingAccountAmortizationService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateAmortizations(@RequestParam String amortizationDate){
        LocalDate date=LocalDate.parse(amortizationDate,formatter);
        List<BillingAccountAmortizationEntity> data= billingAccountAmortizationService.generateAmortizations(date);
        return ResponseEntity.ok(data);
    }
}
