package com.comfandi.phobos.mapper;

import com.comfandi.phobos.entity.BillingAccountEntity;
import com.comfandi.phobos.service.dto.BillingAccountDto;
import org.springframework.stereotype.Component;

@Component
public class BillingAccountMapper {
    public BillingAccountEntity toEntity(BillingAccountDto dto) {
        BillingAccountEntity entity = new BillingAccountEntity();
        entity.setBillingAccountType(dto.getBillingAccountType());
        entity.setBillingAccountName(dto.getBillingAccountName());
        entity.setLaw(dto.getLey());
        entity.setDocumentExcelUrl(dto.getDocumentExcelUrl());
        entity.setDocumentWordUrl(dto.getDocumentWordUrl());
        return entity;
    }
}
