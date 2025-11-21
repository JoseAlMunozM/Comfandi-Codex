package com.comfandi.korlon.services;


import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.*;
import com.comfandi.korlon.manager.data.AmortizationPortfolioGroup;
import com.comfandi.korlon.manager.data.PortfolioGroup;
import com.comfandi.korlon.repositories.BillingAccountAmortizationRepository;
import com.comfandi.korlon.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private BillingAccountAmortizationRepository billingAccountAmortizationRepository;

    @Autowired
    private CourseService courseService;




    public List<PortfolioEntity> find(List<PortfolioGroup> group){
        if(!group.isEmpty()){
            return group.stream().map(x-> portfolioRepository.findById(x.getId())).flatMap(Optional::stream).toList();
        }
        return Collections.emptyList();
    }

    public Boolean delete(Long portfolioId){
        Optional<PortfolioEntity> optPortfolioEntity= portfolioRepository.findById(portfolioId);
        if(optPortfolioEntity.isPresent()){
            portfolioRepository.delete(optPortfolioEntity.get());
            return true;
        }
        return false;
    }

    public List<BillingAccountAmortizationEntity> generateAmortizationGroup(BillingAccountEntity ba){
        return  billingAccountAmortizationRepository.findByAccountId(ba.getBillingAccountId().longValue());
    }

}
