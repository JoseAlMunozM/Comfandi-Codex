package com.comfandi.phobos.service;


import com.comfandi.phobos.entity.PortfolioEntity;
import com.comfandi.phobos.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    public PortfolioEntity save(PortfolioEntity portfolioEntity){
        return portfolioRepository.save(portfolioEntity);
    }

    public Optional<PortfolioEntity> find(Long portfolioId){
        return portfolioRepository.findById(portfolioId);
    }

    public Boolean delete(Long portfolioId){
        Optional<PortfolioEntity> optPortfolioEntity= portfolioRepository.findById(portfolioId);
        if(optPortfolioEntity.isPresent()){
            portfolioRepository.delete(optPortfolioEntity.get());
            return true;
        }
        return false;
    }

}
