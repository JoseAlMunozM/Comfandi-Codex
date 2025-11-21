package com.comfandi.korlon.services;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.comfandi.korlon.api.response.ApiUserNotAcceptableResponse;
import com.comfandi.korlon.entities.BillingAccountAmortizationEntity;
import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.entities.PortfolioEntity;
import com.comfandi.korlon.entities.UserEntity;
import com.comfandi.korlon.enums.FileTypes;
import com.comfandi.korlon.enums.Profiles;
import com.comfandi.korlon.api.request.ApiDocumentEmailRequest;
import com.comfandi.korlon.api.response.ApiDocumentNotificationResponse;
import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.enums.SourceType;
import com.comfandi.korlon.enums.UserStatus;
import com.comfandi.korlon.manager.data.DatosPlantillaGroupTH;
import com.comfandi.korlon.manager.data.UserRevoked;
import com.comfandi.korlon.manager.excel.ExcelManager;
import com.comfandi.korlon.manager.pdf.PDFManager;
import com.comfandi.korlon.mapper.UsersMapper;
import com.comfandi.korlon.utils.aws.S3UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class DocumentsService {

    @Autowired
    private ExcelManager excelManager;
    @Autowired
    private PDFManager pdfManager;
    @Autowired
    private S3UploaderService s3UploaderService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserService userService;
    @Autowired
    private UsersMapper usersMapper;


    public String generateExcelDocument(ArrayList<ApiInformationDatabase> data, Profiles profile, SourceType sourceType,
                                        BillingAccountEntity ba, List<PortfolioEntity> optPortfolioEntity,
                                        Boolean amortizatizable) throws Exception {

        List<BillingAccountAmortizationEntity> portfolio= Collections.emptyList();
        /**if(amortizatizable) {
            portfolio = portfolioService.generateAmortizationGroup(ba);
        }
        if(amortizatizable){**/
            System.out.println("amortization sheet");
            return excelManager.generateAmortizationSheetComfandi(data,profile,ba,optPortfolioEntity);
       /** }
        return excelManager.generateSheetComfandi(data,profile,ba,optPortfolioEntity);**/

    }



    public String generatePDFDocument(ArrayList<ApiInformationDatabase> data,Profiles profiles,SourceType sourceType,BillingAccountEntity ba,List<PortfolioEntity> portfolioList) {

        try {
            String result = pdfManager.generatePDFcomfandiJasper(data,profiles,ba,portfolioList);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return "result";
    }

    public ResponseEntity<ApiDocumentNotificationResponse> sendDocumentByEmail(ApiDocumentEmailRequest request) {
        try {
            boolean success = false;
            String error_message = "";
            if (request.getType().equalsIgnoreCase(FileTypes.PDF.getValue())) {
                try {
                    success = pdfManager.sendPdfFileByEmail(request.getDocumentURL(), request.getDocumentName(), request.getEmail());
                }catch (Exception e) {
                    error_message = e.getMessage();
                    e.printStackTrace();
                }
            } else if (request.getType().equalsIgnoreCase(FileTypes.EXCEL.getValue())) {
                try {
                    success = excelManager.sendExcelFileByEmail(request.getDocumentURL(), request.getDocumentName(), request.getEmail());
                } catch (Exception e) {
                    error_message = e.getMessage();
                    e.printStackTrace();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiDocumentNotificationResponse.builder()
                        .document(request.getDocumentURL())
                        .status("ERROR").error("Error en el tipo de archivo").build());
            }
            return success ? ResponseEntity.ok().body(ApiDocumentNotificationResponse.builder()
                    .document(request.getDocumentURL())
                    .status("OK").error("").build()) : ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiDocumentNotificationResponse.builder()
                    .document(request.getDocumentURL())
                    .status("ERROR").error("Error al enviar el archivo "+error_message).build());
        } catch (
                Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(ApiDocumentNotificationResponse.builder()
                    .document(request.getDocumentURL())
                    .status("ERROR").error(e.getMessage()).build());
        }

    }

    public ResponseEntity sendUnavailableUsersNotification(List<ApiInformationDatabase> data, String email){
        try {
            excelManager.generateSheetUnavailableUsers(data,email);
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");
    }
    public ResponseEntity sendRevokedUsersNotification(List<ApiInformationDatabase> data, String email){
        try {
            excelManager.generateSheetRevokedUsers(data,email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");
    }

    public ResponseInputStream<GetObjectResponse> downloadDocumentByKey(String key) {
        return s3UploaderService.downloadFileAsStream(key);
    }

    public ResponseEntity<List<ApiUserNotAcceptableResponse>> updateUsersByFile (MultipartFile file,Long accountId,boolean revokedUsers){
        List<UserRevoked> users= excelManager.validateRevokedUsersByFile(file,accountId).stream().peek(x->{
                    if(revokedUsers){
                        x.setStatus(UserStatus.NO_COBRADO.getValue());
                    }
                }
        ).toList();
        List<UserEntity> userEntityList=userService.updateUserStatus(users);
        return ResponseEntity.ok(userEntityList.stream().map(x->usersMapper.userEntityToUserNotAcceptable(x)).toList());
    }

    public ResponseEntity<InputStreamResource> getUsersDocument(Long account){
        ByteArrayInputStream excelStream = userService.getUsersByAccountId(account);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }
}
