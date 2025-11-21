package com.comfandi.korlon.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.comfandi.korlon.manager.data.BusinessData;

@Service
public class ExcelService {


    private final BusinessService businessService;

    public ExcelService(BusinessService businessService) {
        this.businessService = businessService;
    }

    public byte[] generateExcel() throws IOException {
        List<BusinessData> business = businessService.getAllBussiness();

        try (InputStream is = getClass().getResourceAsStream("/templates/cuentasEmpresa1.xlsx");
            Workbook workbook = new XSSFWorkbook(is);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            int lastRow = sheet.getLastRowNum();
            for (int i = lastRow; i > 0; i--) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }

            int rowIndex = 1;
            System.out.println("size: "+business.size());

            for (BusinessData b : business) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(b.getNit());
                row.createCell(1).setCellValue(b.getBusinessName());
                row.createCell(2).setCellValue(b.getRepresentativeDocument());
                row.createCell(3).setCellValue(b.getRepresentativeName());
                row.createCell(4).setCellValue(b.getAddress());
                row.createCell(5).setCellValue(b.getTelephone());
                row.createCell(6).setCellValue(b.getCity());
                row.createCell(7).setCellValue(b.getEmail());
                row.createCell(8).setCellValue(b.getRegional());
                row.createCell(9).setCellValue(b.getProgram());
                row.createCell(10).setCellValue(b.getPriceValue());
                row.createCell(11).setCellValue(b.getRegionBilling());
                row.createCell(12).setCellValue(b.getInitialValidationDate());
                row.createCell(13).setCellValue(b.getDeliveryDateBilling());
                row.createCell(14).setCellValue(b.getAdvancePercent());
                row.createCell(15).setCellValue(b.getProgramStartDate());
                row.createCell(16).setCellValue(b.getProgramEndDate());
                row.createCell(17).setCellValue(b.getProvider());
                row.createCell(18).setCellValue(b.getRemissionDate());
                row.createCell(19).setCellValue(b.getTemplateType());
            }


            Sheet sheet2 = workbook.getSheetAt(1);
            int lastRow2 = sheet2.getLastRowNum();
            for (int i = lastRow2; i > 9; i--) {
                Row row = sheet2.getRow(i);
                if (row != null) {
                    sheet2.removeRow(row);
                }
            }

            int rowIndex2 = 9;
            for (BusinessData b : business) {
                Row row = sheet2.createRow(rowIndex2++);
                row.createCell(0).setCellValue(b.getRegionBilling()); 
                row.createCell(1).setCellValue(b.getProgram());       
                row.createCell(2).setCellValue(b.getNit());           
                row.createCell(3).setCellValue(b.getPriceValue());    
                row.createCell(4).setCellValue(b.getNit());           
                row.createCell(5).setCellValue(" ");  
                row.createCell(6).setCellValue(" ");  
                row.createCell(7).setCellValue(b.getCebe());          
            }
            
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}
