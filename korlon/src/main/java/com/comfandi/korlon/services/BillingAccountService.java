package com.comfandi.korlon.services;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.entities.UserEntity;
import com.comfandi.korlon.enums.Profiles;
import com.comfandi.korlon.repositories.BillingAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingAccountService {
    @Autowired
    private BillingAccountRepository billingAccountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private  WorkshopUserService workshopUserService;

    @Transactional
    public BillingAccountEntity createBillingAcount(List<ApiInformationDatabase> users, String law,Profiles profile) {

        BillingAccountEntity ba = new BillingAccountEntity();
        ba.setBillingAccountId(null);
        ba.setLaw(law);
        validateAccountName(ba,profile);
        BillingAccountEntity billingAccount = billingAccountRepository.save(ba);
        if(profile==Profiles.TH_FOSFEC){
            workshopUserService.updateAccountId(users,billingAccount);
        }else{
            userService.updateAccountId(users, billingAccount);
        }

        return billingAccount;
    }
    public BillingAccountEntity updateBillingAccount(Integer billingAccountId,String urlExcel,String urlPDF){
        Optional<BillingAccountEntity> optba= billingAccountRepository.findById(billingAccountId.longValue());
        if(optba.isPresent()){
            BillingAccountEntity ba =optba.get();
            ba.setDocumentWordUrl(urlPDF);
            ba.setDocumentExcelUrl(urlExcel);
            return billingAccountRepository.save(ba);
        }
        return null;
    }

    public  BillingAccountEntity getBillingAccount(Long accountId){
        Optional<BillingAccountEntity> optBa=billingAccountRepository.findById(accountId);
        return optBa.orElse(null);
    }

    private void validateAccountName(BillingAccountEntity ba, Profiles profile) {
        ba.setBillingAccountName(profile.getValue());
        if (profile== Profiles.ACTIVOS_EMPRESARIAL) {
                ba.setBillingAccountType("Empleabilidad");
        }else if(profile== Profiles.CESANTES_EMPRESARIAL) {
                ba.setBillingAccountType("Cesantes");
        }else if(profile==Profiles.TH_FOSFEC){
            ba.setBillingAccountType("Talleres");
        }
    }



}
