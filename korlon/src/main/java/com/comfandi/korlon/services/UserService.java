package com.comfandi.korlon.services;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.entities.UserEntity;
import com.comfandi.korlon.manager.data.UserRevoked;
import com.comfandi.korlon.repositories.UserRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public void updateAccountId(List<ApiInformationDatabase> users, BillingAccountEntity account) {
        for (ApiInformationDatabase user : users) {
            userRepository.updateIdAccounts(user.getUserId(), account.getBillingAccountId().longValue(), LocalDate.now());
        }
    }

    public List<UserEntity> updateUserStatus(List<UserRevoked> users) {
        List<UserEntity> userEntityList = new ArrayList<>();
        for (UserRevoked userRevoked : users) {
            Optional<UserEntity> optUserEntity = userRepository.getUserByAccountId(userRevoked.getAccountId(), userRevoked.getDocument());
            if (optUserEntity.isPresent()) {

                UserEntity userEntity = optUserEntity.get();
                userEntity.setStatus(userRevoked.getStatus());
                userEntity.setDescription(userRevoked.getReason());
                userEntityList.add(userRepository.save(userEntity));
            }

        }
        return userEntityList;
    }

    public ByteArrayInputStream getUsersByAccountId(long accountId) {
        List<UserEntity> users = userRepository.getUsersByAccountId(accountId);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Usuarios");
            // Cabecera
            XSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("Cuenta");
            header.createCell(0).setCellValue("Identificacion");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Estado");
            header.createCell(3).setCellValue("Validacion");
            int index = 0;
            XSSFRow row;
            for (UserEntity userEntity : users) {
                row = sheet.createRow(index++);
                row.createCell(0).setCellValue(userEntity.getAccountId().toString());
                row.createCell(0).setCellValue(userEntity.getIdentificationNumber());
                row.createCell(1).setCellValue(userEntity.getName());
                row.createCell(2).setCellValue(userEntity.getStatus());
                row.createCell(3).setCellValue(userEntity.getDescription());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }
}
