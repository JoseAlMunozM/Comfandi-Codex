package com.comfandi.korlon.manager.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessData {

    private String nit;
    private String businessName;
    private String representativeDocument;
    private String representativeName;
    private String address;
    private String telephone;
    private String city;
    private String email;
    private String regional;
    private String program;
    private String priceValue;
    private String regionBilling;
    private String initialValidationDate;
    private String deliveryDateBilling;
    private String advancePercent;
    private String programStartDate;
    private String programEndDate;
    private String provider;
    private String remissionDate;
    private String templateType;
    private String cebe;
    private String mode;
    private Long  cant;
    private Double sum;


}