package com.comfandi.korlon.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.comfandi.korlon.manager.data.BusinessData;
import com.comfandi.korlon.repositories.BusinessRepository;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public List<BusinessData> getAllBussiness() {
        return businessRepository.findAll();
    }
}
