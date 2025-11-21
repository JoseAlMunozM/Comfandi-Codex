package com.comfandi.phobos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiEmailNotificationRequest;
import com.comfandi.phobos.client.response.ApiDocumentNotificationResponse;
import com.comfandi.phobos.client.response.BillingApprovalResponse;
import com.comfandi.phobos.entity.BillingAccountEntity;
import com.comfandi.phobos.mapper.BillingAccountApprovalMapper;
import com.comfandi.phobos.service.enums.BillingApprovalPersonType;
import com.comfandi.phobos.service.enums.BillingApprovalStatus;
import com.comfandi.phobos.service.enums.FileTypes;
import com.comfandi.phobos.util.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.comfandi.phobos.entity.BillingApprovalEntity;
import com.comfandi.phobos.repository.BillingApprovalRepository;
import com.comfandi.phobos.service.dto.UpdateBillingApprovalDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BillingApprovalService {

    @Autowired
    private BillingApprovalRepository billingApprovalRepository;

    @Autowired
    private BillingAccountApprovalMapper billingAccountApprovalMapper;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private BillingAccountServices billingAccountServices;

    @Autowired
    private ApiConfig apiConfig;

    private static final Set<String> validApprovalValues = Set.of("APROBADO", "RECHAZADO");

    public ResponseEntity<?> updateBillingApproval(UpdateBillingApprovalDto billingApproval) throws Exception {
        Optional<BillingApprovalEntity> billingApprovalEntityOpt = billingApprovalRepository.findBillingApprovalByidAccount(
                billingApproval.getBillingAccountId().longValue(),
                billingApproval.getUserType());
        if (billingApprovalEntityOpt.isPresent()) {
            if (validApprovalValues.contains(billingApprovalEntityOpt.get().getApproval())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Cuenta de cobro " + billingApproval.getBillingAccountId().longValue() + " ya gestionada por " + billingApproval.getUserType());
            }
        }
        BillingApprovalEntity billingApprovalEntity = BillingApprovalEntity.builder()
                .billingAccountId(billingApproval.getBillingAccountId())
                .approvalDate(LocalDateTime.now())
                .emailSentDate(null)//todo: validar cuando cambiar esto
                .identificationNumber(Integer.valueOf(billingApproval.getIdentificationNumber()))
                .userType(billingApproval.getUserType())
                .approval(billingApproval.getApproval())
                .observation(billingApproval.getObservation())
                .build();

        BillingApprovalEntity newBillingApprovalEntity = billingApprovalRepository.save(billingApprovalEntity);
        boolean emailSent=validateBillingApproval(billingApproval.getBillingAccountId().longValue());

        BillingApprovalResponse response=billingAccountApprovalMapper.billingApprovalEntityToBillingApprovalResponse(newBillingApprovalEntity);
        response.setEmailSent(emailSent?"Yes":"No");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * @param idAccount: Billing Account identified
     * Checks if a Billing Approval was validated
     * by JEFE and ANALISTA both need to be ACCEPTED to send email
     */
    public boolean validateBillingApproval(Long idAccount) {
        Optional<BillingApprovalEntity> billingApprovalEntityByJefe = billingApprovalRepository.findBillingApprovalByidAccount(idAccount, BillingApprovalPersonType.JEFE.getValue());
        Optional<BillingApprovalEntity> billingApprovalEntityByAnalista = billingApprovalRepository.findBillingApprovalByidAccount(idAccount, BillingApprovalPersonType.ANALISTA.getValue());
        if (billingApprovalEntityByAnalista.isPresent() && billingApprovalEntityByJefe.isPresent()) {
            if(billingApprovalEntityByJefe.get().getApproval().equalsIgnoreCase(BillingApprovalStatus.APPROVED.getValue()) &&
            billingApprovalEntityByAnalista.get().getApproval().equalsIgnoreCase(BillingApprovalStatus.APPROVED.getValue())) {
                ApiDocumentNotificationResponse pdf=sentDocumentNotification(idAccount, FileTypes.PDF.getValue());
                ApiDocumentNotificationResponse excel=sentDocumentNotification(idAccount,FileTypes.EXCEL.getValue());
                if(pdf==null || excel==null) {
                    return false;
                }
                return pdf.getStatus().equalsIgnoreCase("OK") && excel.getStatus().equalsIgnoreCase("OK");
            }
        }
        return true;
    }

    private ApiDocumentNotificationResponse sentDocumentNotification( Long idAccount,String type) {
        BillingAccountEntity BillingAccount = billingAccountServices
                .getBillingAccount(idAccount);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        ApiEmailNotificationRequest request= ApiEmailNotificationRequest
                .builder()
                .email(apiConfig.getEmailNotification())//todo: revisar que correo se envia
                .documentName("document_"+LocalDate.now().format(formatter))
                .documentURL(getBillingAccountUrl(type,BillingAccount))
                .type(type).build();
        return externalServiceClient.fetchKorlonEmailNotificationFromService(request);
    }

    private String getBillingAccountUrl(String type,BillingAccountEntity billingAccount) {
        FileTypes fileType=FileTypes.valueOf(type.toUpperCase());
        return switch (fileType){
            case PDF->billingAccount.getDocumentWordUrl();
            case EXCEL-> billingAccount.getDocumentExcelUrl();
        };
    }


    public Optional<BillingApprovalEntity> getBillingApprovalByIdAccount(Long idAccount, String userType) {
        return billingApprovalRepository.findBillingApprovalByidAccount(idAccount, userType);
    }
}
