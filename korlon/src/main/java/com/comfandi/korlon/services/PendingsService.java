package com.comfandi.korlon.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.repositories.PendingRepository;

@Service
public class PendingsService {

    @Autowired
    private PendingRepository pendingRepository;

    public List<BillingAccountEntity> obtenerEmpresasPendientesCobro() {
        return pendingRepository.findByCompleteFalse();
    }
}
