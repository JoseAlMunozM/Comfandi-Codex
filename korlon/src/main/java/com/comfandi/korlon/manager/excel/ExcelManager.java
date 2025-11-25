package com.comfandi.korlon.manager.excel;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.entities.PortfolioEntity;
import com.comfandi.korlon.enums.Profiles;
import com.comfandi.korlon.entities.TypificationEntity;
import com.comfandi.korlon.manager.data.*;
import com.comfandi.korlon.services.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.utils.aws.AwsSESmanager;
import com.comfandi.korlon.utils.aws.S3UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.sound.sampled.Port;

@Component
public class ExcelManager {

    private static final Logger log = LoggerFactory.getLogger(ExcelManager.class);

    @Value("${app.file-path-template}")
    private String templatePath;

    @Value("${app.file-unavailable-path-template}")
    private String unavailablePath;

    private final S3UploaderService s3UploaderService;

    private final AwsSESmanager awsSESmanager;
    @Value("${app.file-path-download}")
    private String downloadPath;

    @Autowired
    private TypificationService typificationService;

    @Autowired
    private InfoPlantillaContableService infoPlantillaContableService;

    @Autowired
    private InfoValidacionPlantillaService infoValidacionPlantillaService;

    @Autowired
    private InfoDatosPlantillaService infoDatosPlanilla;

    @Autowired
    private PortfolioService portfolioService;

    public ExcelManager(S3UploaderService s3UploaderService, AwsSESmanager awsSESmanager) {
        this.s3UploaderService = s3UploaderService;
        this.awsSESmanager = awsSESmanager;
    }

    public void testMod() {
        System.out.println("Running Excel module!!");
    }


    private String generateFileName(String profile) {

        String isoSafeFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
        String filename = "reporte_"+profile+"_"+  isoSafeFilename; // + ".xlsx";
        return filename;

    }

    public String generateAmortizationSheetComfandi(ArrayList<ApiInformationDatabase> rows,Profiles profiles,
                                                    BillingAccountEntity ba,List<PortfolioEntity> optPortfolio){
        try{
            String templateUrl=templatePath + "template_amortization.xlsx";
            ClassPathResource resource = new ClassPathResource(templateUrl);
            List<TypificationEntity> typificationEntityList = typificationService.getTypificationByType(profiles.getValue());
            try (InputStream fis = resource.getInputStream();
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
                // Obtener la hoja DB
                XSSFSheet sheet = workbook.getSheetAt(0);
                ExcelManager.log.info("Sheet info: " + sheet.getSheetName());
                ExcelManager.log.info("Sheet DB ");
                int lastRowNum = 1;
                for (ApiInformationDatabase row : rows) {
                    XSSFRow newRow = sheet.createRow(lastRowNum++);
                    newRow.setRowStyle(geteCellStyle(workbook));
                    this.addDBAmortizationSheetRowData(row, newRow,profiles,optPortfolio);
                }

                // Obtener la hoja Plantilla Amortizacion
                ExcelManager.log.info("Sheet Datos plantilla Amortizacion ");
                sheet = workbook.getSheetAt(1);
                DatosPlantillaHeader header= generateDatosPlantillaHeaderAmortizacion(rows,ba);
                List<DatosPlantilla> datosPlantillas= infoDatosPlanilla.generateDatosPLantillaTH(rows,typificationEntityList,header,true,optPortfolio);
                addInfoPlanillaInfo(datosPlantillas,header, sheet,profiles,true);

                ExcelManager.log.info("Sheet Plantilla Contable Amortizacion ");
                // Datos Planilla Contable
                sheet = workbook.getSheetAt(2);
                Map<Integer,List<InfoPlantillaContableRow>> plantillaContableList= infoPlantillaContableService.generatePlanillaContable(datosPlantillas,typificationEntityList,header,profiles.getValue(),true);

                lastRowNum = 1;
                for (Map.Entry<Integer, List<InfoPlantillaContableRow>> groupRows : plantillaContableList
                        .entrySet()) {

                    for (InfoPlantillaContableRow row : groupRows.getValue()) {
                        XSSFRow newRow = sheet.createRow(lastRowNum++);
                        this.addInfoPlanillaContableRow(row, newRow);
                    }

                    lastRowNum = lastRowNum + 1;
                }

                ExcelManager.log.info("Sheet Validacion Plantilla Amortizacion ");
                //datos validacion plantilla
                List<InfoValidacionPlantillaRow> plantillValidacionList = infoValidacionPlantillaService.generarDatosValidacionPlantilla(datosPlantillas,typificationEntityList,profiles.getValue());
                sheet= workbook.getSheetAt(3);
                int rowNumber=2;
                XSSFRow newRow=null;

                for(InfoValidacionPlantillaRow row:plantillValidacionList){
                    rowNumber++;
                    newRow=sheet.createRow(rowNumber);
                    this.addInfoValidacionPlantilla(newRow,row,geteCellStyle(workbook));
                }

                ExcelManager.log.info("Sheet Tipificacion Amortizacion ");
                //Datos Tipificacion
                //todo: revisar como popular esta parte
                sheet = workbook.getSheetAt(4);
                System.out.println("sheet "+sheet.getSheetName());
                TypificationEntity typificationEntity = null;
                for (int i = 4; i <= 10; i++) {
                    XSSFRow row = sheet.getRow(i);
                    if(row!=null){
                        String location = row.getCell(1).getStringCellValue();
                        if(!location.isEmpty()) {
                            typificationEntity = typificationEntityList.stream().filter(x -> x.getLocation().equalsIgnoreCase(location)).findFirst().orElse(null);
                            if (typificationEntity != null) {
                                XSSFCell cebe = row.getCell(2);
                                cebe.setCellValue(typificationEntity.getCebe());
                                XSSFCell account = row.getCell(3);
                                account.setCellValue(typificationEntity.getAccount());
                                XSSFCell assigment = row.getCell(4);
                                assigment.setCellValue(typificationEntity.getAssignment());
                            }
                        }
                    }
                }
                //generate portfolio
                int j=4;
                CellStyle style = this.geteCellStyle(workbook);
                for(PortfolioEntity portfolio:optPortfolio){
                    XSSFRow row = sheet.getRow(j);
                    if(row!=null){
                        XSSFCell name = row.createCell(7);
                        name.setCellValue(portfolio.getName());
                        name.setCellStyle(style);
                        XSSFCell resumedName = row.createCell(8);
                        resumedName.setCellValue("");
                        resumedName.setCellStyle(style);
                        XSSFCell modality = row.createCell(9);
                        modality.setCellValue(portfolio.getModality().getName());
                        modality.setCellStyle(style);
                        XSSFCell productKey = row.createCell(10);
                        productKey.setCellValue("");
                        productKey.setCellStyle(style);
                        XSSFCell fee = row.createCell(11);
                        fee.setCellValue(portfolio.getValueFee());
                        fee.setCellStyle(style);
                        XSSFCell account = row.createCell(12);
                        account.setCellValue(portfolio.getAccount());
                        account.setCellStyle(style);
                        j++;
                    }
                }

                // Guardar el archivo
                String fileName = this.generateFileName("amortizacion-"+profiles.name());
                File tempFile = new File(downloadPath + fileName + ".xlsx");

                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    workbook.write(fos);
                    ExcelManager.log.debug("Archivo Excel generado exitosamente.");
                }

                String s3Url = this.writeAndUpload(s3UploaderService, tempFile);
                return s3Url;



            } catch (IOException e) {
            ExcelManager.log.error("Error al procesar el archivo Excel:");
            e.printStackTrace();
            return "{ status: 'Error' data:'" + e.getMessage() + "}";
            }
        }catch (Exception e){

        }
        return "";
    }

    public String generateSheetComfandi(ArrayList<ApiInformationDatabase> rows,
                                        Profiles profiles, BillingAccountEntity ba, List<PortfolioEntity> optPortfolio) throws Exception {

    System.out.println(">>> ENTRÓ AL MÉTODO generateSheetComfandi");

    if (profiles == null) {
        System.out.println("ERROR → profiles es NULL");
        throw new Exception("profiles es NULL");
    }

        try {
        boolean isProfileTH = profiles.getValue()
                .equals(Profiles.TH_FOSFEC.getValue());

        System.out.println("Paso 1 → perfil evaluado");

        String templateName = isProfileTH
            ? "template_TH_Fosfec24A.xlsx"
            : "template_active_workers.xlsx";

        System.out.println("Paso 2 → templateName = " + templateName);

        ClassPathResource resource = new ClassPathResource("templates/" + templateName);

        System.out.println("Paso 3 → ClassPathResource creado");
        System.out.println("EXISTS? → " + resource.exists());

            List<TypificationEntity> typificationEntityList = typificationService.getTypificationByType(profiles.getValue());
            try (InputStream fis = resource.getInputStream();
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
                // Obtener la hoja DB
                XSSFSheet sheet = workbook.getSheetAt(0);
                // Agregar una nueva fila al final
                int lastRowNum = 1;

                ExcelManager.log.info("Sheet info: " + sheet.getSheetName());
                ExcelManager.log.info("LAST ROW: " + lastRowNum);

                for (ApiInformationDatabase row : rows) {
                    XSSFRow newRow = sheet.createRow(lastRowNum++);
                    newRow.setRowStyle(geteCellStyle(workbook));
                    this.addDBSheetRowData(row, newRow,profiles,optPortfolio);
                }

                // Datos Planilla
                sheet = workbook.getSheetAt(1);

                DatosPlantillaHeader header= generateDatosPlantillaHeader(rows,ba,profiles);


                List<DatosPlantilla> datosPlantillas= infoDatosPlanilla.generateDatosPLantillaTH(rows,typificationEntityList,header,false,optPortfolio);
                addInfoPlanillaInfo(datosPlantillas,header, sheet,profiles,false);


                ExcelManager.log.info(datosPlantillas.toString());

                // Datos Planilla Contable
                sheet = workbook.getSheetAt(2);
                Map<Integer,List<InfoPlantillaContableRow>> plantillaContableList= infoPlantillaContableService.generatePlanillaContable(datosPlantillas,typificationEntityList,header,profiles.getValue(),false);

                lastRowNum = 1;
                for (Map.Entry<Integer, List<InfoPlantillaContableRow>> groupRows : plantillaContableList
                        .entrySet()) {

                    for (InfoPlantillaContableRow row : groupRows.getValue()) {
                        XSSFRow newRow = sheet.createRow(lastRowNum++);
                        this.addInfoPlanillaContableRow(row, newRow);
                    }

                    lastRowNum = lastRowNum + 1;
                }

                ExcelManager.log.info("Planilla contable: " + plantillaContableList.size());

                //Datos Tipificacion
                //todo: revisar como popular esta parte
                sheet = workbook.getSheetAt(3);
                System.out.println("sheet "+sheet.getSheetName());
                TypificationEntity typificationEntity = null;
                for (int i = 4; i <= 10; i++) {
                    XSSFRow row = sheet.getRow(i);
                    if(row!=null){
                        String location = row.getCell(1).getStringCellValue();
                        if(!location.isEmpty()) {
                            typificationEntity = typificationEntityList.stream().filter(x -> x.getLocation().equalsIgnoreCase(location)).findFirst().orElse(null);
                            if (typificationEntity != null) {
                                XSSFCell cebe = row.getCell(2);
                                cebe.setCellValue(typificationEntity.getCebe());
                                XSSFCell account = row.getCell(3);
                                account.setCellValue(typificationEntity.getAccount());
                                XSSFCell assigment = row.getCell(4);
                                assigment.setCellValue(typificationEntity.getAssignment());
                            }
                        }
                    }
                }
                //generate portfolio
                int j=4;
                CellStyle style = this.geteCellStyle(workbook);
                for(PortfolioEntity portfolio:optPortfolio){
                    XSSFRow row = sheet.getRow(j);
                    if(row!=null){
                        XSSFCell name = row.createCell(7);
                        name.setCellValue(portfolio.getName());
                        name.setCellStyle(style);
                        XSSFCell resumedName = row.createCell(8);
                        resumedName.setCellValue("");
                        resumedName.setCellStyle(style);
                        XSSFCell modality = row.createCell(9);
                        modality.setCellValue(portfolio.getModality().getName());
                        modality.setCellStyle(style);
                        XSSFCell productKey = row.createCell(10);
                        productKey.setCellValue("");
                        productKey.setCellStyle(style);
                        XSSFCell fee = row.createCell(11);
                        fee.setCellValue(portfolio.getValueFee());
                        fee.setCellStyle(style);
                        XSSFCell account = row.createCell(12);
                        account.setCellValue(portfolio.getAccount());
                        account.setCellStyle(style);
                        j++;
                    }
                }

                // Guardar el archivo
                String fileName = this.generateFileName(profiles.name());
                File tempFile = new File(downloadPath + fileName + ".xlsx");

                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    workbook.write(fos);
                    ExcelManager.log.debug("Archivo Excel generado exitosamente.");
                }

                String s3Url = this.writeAndUpload(s3UploaderService, tempFile);
                //this.sendPdfEmail(tempFile);
                return s3Url;


            } catch (IOException e) {
                ExcelManager.log.error("Error al procesar el archivo Excel:");
                e.printStackTrace();
                return "{ status: 'Error' data:'" + e.getMessage() + "}";
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExcelManager.log.error("Error on generating the excel file:" + e.getMessage());
            return "{ status: 'Error' data:'" + e.getMessage() + "}";
        }
    }
    private CellStyle geteCellStyle( XSSFWorkbook workbook){
        CellStyle style = workbook.createCellStyle();
        // Configurar bordes
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Opcional: Color de los bordes
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    private XSSFRow addDBAmortizationSheetRowData(ApiInformationDatabase row, XSSFRow newRow, Profiles profile,List<PortfolioEntity> optPortfolio){
        System.out.println("profile "+profile);
        if (profile == Profiles.ACTIVOS_EDUCACION || profile == Profiles.ACTIVOS_EMPRESARIAL){
            System.out.println("nit "+row.getNit());
            newRow.createCell(0).setCellValue(row.getNit());
            newRow.createCell(1).setCellValue(row.getEmpresa());
        }
        newRow.createCell(2).setCellValue(row.getCedula());
        newRow.createCell(3).setCellValue(row.getNombreCompleto());
        newRow.createCell(5).setCellValue(row.getTelefono());
        newRow.createCell(6).setCellValue(row.getCiudad());
        newRow.createCell(7).setCellValue(row.getCorreoElectronico());
        newRow.createCell(8).setCellValue(row.getNombreDelPrograma());
        newRow.createCell(9).setCellValue(row.getModalidad());
        setCellNumeric(newRow.createCell(10), toBigDecimal(row.getValor()));
        Optional<PortfolioEntity> portfolio= optPortfolio.stream().filter(x-> x.getId().longValue()==Long.parseLong(row.getPortfolioId())).findFirst();
        if(portfolio.isPresent()){
            Double valor = safeDouble(row.getValor());
            Double dp1 = portfolio.get().getDownPayment1();
            if (valor != null && dp1 != null) {
                setCellNumeric(newRow.createCell(11), BigDecimal.valueOf((valor * dp1) / 100));
            }
            setCellNumeric(newRow.createCell(14), toBigDecimal(dp1));
            if(portfolio.get().getDownPayment2()!=null){
                Double dp2 = portfolio.get().getDownPayment2();
                if (valor != null && dp2 != null) {
                    setCellNumeric(newRow.createCell(12), BigDecimal.valueOf((valor * dp2) / 100));
                }
                setCellNumeric(newRow.createCell(15), toBigDecimal(dp2));
            }
            if(portfolio.get().getDownPayment3()!=null){
                Double dp3 = portfolio.get().getDownPayment3();
                if (valor != null && dp3 != null) {
                    setCellNumeric(newRow.createCell(13), BigDecimal.valueOf((valor * dp3) / 100));
                }
                setCellNumeric(newRow.createCell(16), toBigDecimal(dp3));
            }
        }
        newRow.createCell(17).setCellValue(row.getRegionalCobro());
        newRow.createCell(18).setCellValue(row.getRegionalDomicilio());
        newRow.createCell(19).setCellValue(row.getObservacionBeneficiarioCotizante());
        newRow.createCell(20).setCellValue(row.getNumeroDeDocumentoDeCotizante());
        newRow.createCell(21).setCellValue(row.getCategoria());
        newRow.createCell(22).setCellValue(row.getFechaValidacionInicial());
        newRow.createCell(23).setCellValue(row.getValidacionRevalidacionAfiliacionACaja());
        newRow.createCell(24).setCellValue(row.getValidadoPor());
        newRow.createCell(25).setCellValue(row.getFechaEnvioBdCobro());
        newRow.createCell(26).setCellValue(row.getPorcentajeAvanceDelPrograma());
        newRow.createCell(27).setCellValue(row.getFechaDeInicioDelPrograma());
        newRow.createCell(28).setCellValue(row.getFechaDeFinalizacionDelPrograma());
        newRow.createCell(29).setCellValue(row.getFechaDeRegistroSise());
        newRow.createCell(30).setCellValue(row.getFechaEvaluacionSise());
        newRow.createCell(31).setCellValue(row.getProveedor());
        newRow.createCell(32).setCellValue(row.getFechaRemision());
        newRow.createCell(33).setCellValue(row.getAfiliada());
        newRow.createCell(34).setCellValue(row.getFechaDeAfiliacion());
        newRow.createCell(35).setCellValue(row.getFechaValidacionAfiliacion());
        newRow.createCell(36).setCellValue(row.getEstadoParafiscales());
        newRow.createCell(37).setCellValue(row.getFechaValidacionParafiscales());
        newRow.createCell(38).setCellValue(row.getNombreDeQuienRealizaLaValidacionAfiliacion());
        newRow.createCell(39).setCellValue(row.getNombreDeQuienRealizaValidacionParafiscales());
        newRow.createCell(40).setCellValue(row.getNumeroColaboradores());
        newRow.createCell(41).setCellValue(row.getTipoDePlantilla());
        newRow.createCell(42).setCellValue(row.getCharge());
        newRow.createCell(43).setCellValue(row.getObservations());
        return newRow;
    }

    private XSSFRow addDBSheetRowData(ApiInformationDatabase row, XSSFRow newRow, Profiles profile,List<PortfolioEntity> optPortfolio) {

        ExcelManager.log.info("Added nit: " + row.getNit() + " row# " + newRow.getRowNum());
        System.out.println("profile "+profile);
        if (profile == Profiles.ACTIVOS_EDUCACION || profile == Profiles.ACTIVOS_EMPRESARIAL){
            System.out.println("nit "+row.getNit());
            newRow.createCell(0).setCellValue(row.getNit());
            newRow.createCell(1).setCellValue(row.getEmpresa());
            newRow.createCell(4).setCellValue(row.getDireccion());
        }
        newRow.createCell(2).setCellValue(row.getCedula());
        newRow.createCell(3).setCellValue(row.getNombreCompleto());
        newRow.createCell(5).setCellValue(row.getTelefono());
        newRow.createCell(6).setCellValue(row.getCiudad());
        newRow.createCell(7).setCellValue(row.getCorreoElectronico());
        newRow.createCell(8).setCellValue(row.getNombreDelPrograma());
        newRow.createCell(9).setCellValue(row.getModalidad());
        setCellNumeric(newRow.createCell(10), toBigDecimal(row.getValor()));
        newRow.createCell(11).setCellValue(row.getRegionalCobro());
        newRow.createCell(12).setCellValue(row.getRegionalDomicilio());
        newRow.createCell(13).setCellValue(row.getObservacionBeneficiarioCotizante());
        newRow.createCell(14).setCellValue(row.getNumeroDeDocumentoDeCotizante());
        newRow.createCell(15).setCellValue(row.getCategoria());
        newRow.createCell(16).setCellValue(row.getFechaValidacionInicial());
        newRow.createCell(17).setCellValue(row.getValidacionRevalidacionAfiliacionACaja());
        newRow.createCell(18).setCellValue(row.getValidadoPor());
        newRow.createCell(19).setCellValue(row.getFechaEnvioBdCobro());
        newRow.createCell(20).setCellValue(row.getPorcentajeAvanceDelPrograma());
        newRow.createCell(21).setCellValue(row.getFechaDeInicioDelPrograma());
        newRow.createCell(22).setCellValue(row.getFechaDeFinalizacionDelPrograma());
        newRow.createCell(23).setCellValue(row.getFechaDeRegistroSise());
        newRow.createCell(24).setCellValue(row.getFechaEvaluacionSise());
        newRow.createCell(25).setCellValue(row.getProveedor());
        newRow.createCell(26).setCellValue(row.getFechaRemision());
        newRow.createCell(27).setCellValue(row.getAfiliada());
        newRow.createCell(28).setCellValue(row.getFechaDeAfiliacion());
        newRow.createCell(29).setCellValue(row.getFechaValidacionAfiliacion());
        newRow.createCell(30).setCellValue(row.getEstadoParafiscales());
        newRow.createCell(31).setCellValue(row.getFechaValidacionParafiscales());
        newRow.createCell(32).setCellValue(row.getNombreDeQuienRealizaLaValidacionAfiliacion());
        newRow.createCell(33).setCellValue(row.getNombreDeQuienRealizaValidacionParafiscales());
        newRow.createCell(34).setCellValue(row.getNumeroColaboradores());
        newRow.createCell(35).setCellValue(row.getTipoDePlantilla());
        newRow.createCell(36).setCellValue(row.getCharge());
        newRow.createCell(37).setCellValue(row.getObservations());
        return newRow;

    }

    private XSSFRow addInfoPlanillaRow(DatosPlantilla dataRow, XSSFRow newRow,Profiles profile,Boolean amortizable) {

        ExcelManager.log.info("Added reg: " + dataRow.getRegional() + " row# " + newRow.getRowNum());

        newRow.createCell(0).setCellValue(dataRow.getRegional());
        newRow.createCell(1).setCellValue(dataRow.getProgram());
        newRow.createCell(2).setCellValue(dataRow.getMode());
        setCellNumeric(newRow.createCell(3), toBigDecimal(dataRow.getSum()));
        newRow.createCell(4).setCellValue(dataRow.getProgramResume());
        newRow.createCell(5).setCellValue(dataRow.getResume());
        setCellNumeric(newRow.createCell(6), toBigDecimal(dataRow.getCant()));
        newRow.createCell(7).setCellValue(dataRow.getAccount());
        newRow.createCell(8).setCellValue(dataRow.getCebe());
        newRow.createCell(9).setCellValue(dataRow.getTextRegistry());
        newRow.createCell(10).setCellValue(dataRow.getAssignation());
        setCellNumeric(newRow.createCell(11), toBigDecimal(dataRow.getTextLength()));
        if(amortizable) {
            setCellNumeric(newRow.createCell(12), toBigDecimal(dataRow.getDownPayment1()));
            setCellNumeric(newRow.createCell(13), toBigDecimal(dataRow.getDownPayment2()));
            setCellNumeric(newRow.createCell(14), toBigDecimal(dataRow.getDownPayment3()));
        }

        return newRow;
    }

    private XSSFRow addInfoPlanillaTotalRow(XSSFRow newRow, double sumaValor, int sumaCedula,Profiles profile) {

        String TOTAL_LABEL = "Total general";

        ExcelManager.log.info("Added Total: " + sumaValor + " row# " + newRow.getRowNum());

        newRow.createCell(0).setCellValue(TOTAL_LABEL);
        newRow.createCell(1).setCellValue("");
        setCellNumeric(newRow.createCell(3), toBigDecimal(sumaValor));
        setCellNumeric(newRow.createCell(6), toBigDecimal(sumaCedula));



        return newRow;
    }


    private XSSFRow addInfoValidacionPlantilla(XSSFRow newRow,InfoValidacionPlantillaRow infoValidacionPlantillaRow,CellStyle style){
        XSSFCell item=newRow.createCell(0);
        item.setCellValue(infoValidacionPlantillaRow.getItem());
        item.setCellStyle(style);
        XSSFCell clave=newRow.createCell(1);
        clave.setCellValue(infoValidacionPlantillaRow.getClave());
        clave.setCellStyle(style);
        XSSFCell account= newRow.createCell(2);
        account.setCellValue(infoValidacionPlantillaRow.getAccount());
        account.setCellStyle(style);
        XSSFCell asignacion=newRow.createCell(3);
        asignacion.setCellValue(infoValidacionPlantillaRow.getAsignation());
        asignacion.setCellStyle(style);
        XSSFCell cebe=newRow.createCell(4);
        cebe.setCellValue(infoValidacionPlantillaRow.getCebe());
        cebe.setCellStyle(style);
        XSSFCell texto=newRow.createCell(5);
        texto.setCellValue(infoValidacionPlantillaRow.getText());
        texto.setCellStyle(style);
        XSSFCell valor=newRow.createCell(6);
        valor.setCellValue(infoValidacionPlantillaRow.getSumValue().toString());
        valor.setCellStyle(style);
        return newRow;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof BigDecimal bd) {
                return bd;
            }
            if (value instanceof Number num) {
                if (num instanceof Long || num instanceof Integer) {
                    return BigDecimal.valueOf(num.longValue());
                }
                return BigDecimal.valueOf(num.doubleValue());
            }
            String raw = value.toString().trim();
            if (raw.isEmpty()) {
                return null;
            }
            // replace comma as decimal separator if needed
            raw = raw.replace(",", "");
            return new BigDecimal(raw);
        } catch (Exception e) {
            return null;
        }
    }

    private void setCellNumeric(XSSFCell cell, BigDecimal value) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        cell.setCellValue(value.toPlainString());
    }

    private Double safeDouble(String value) {
        try {
            return value == null ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public DatosPlantillaHeader generateDatosPlantillaHeaderAmortizacion(ArrayList<ApiInformationDatabase> rows,BillingAccountEntity ba){
        String conceptValue="CXC %d-2 CAPAC";
        String concept=String.format(conceptValue,ba.getBillingAccountId());
        String program ="PROG CREACTIVATE";
        String date=LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String concepObject = "PERS";
        String etapa="ETAPA";
        return DatosPlantillaHeader.builder()
                .concept(concept)
                .program(program)
                .date(date)
                .conceptObject(concepObject)
                .etapa(etapa)
                .build();
    }

    public DatosPlantillaHeader generateDatosPlantillaHeader(ArrayList<ApiInformationDatabase> rows,BillingAccountEntity ba,Profiles profiles){
        String conceptValue=profiles==Profiles.TH_FOSFEC?"CXC %dA REALIZACIÓN TALLERES OCUPACIONALES":"CXC %d-2 CAPAC";
        String concept=String.format(conceptValue,ba.getBillingAccountId());
        String program = profiles==Profiles.TH_FOSFEC?"":"PROG CREACTIVATE";
        String date=LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String concepObject =  profiles==Profiles.TH_FOSFEC?"":"PERS";
        return DatosPlantillaHeader.builder()
                .concept(concept)
                .program(program)
                .date(date)
                .conceptObject(concepObject)
                .build();
    }

    private XSSFSheet addInfoPlanillaInfo(List<DatosPlantilla> dataPlantilla,DatosPlantillaHeader header,
                                          XSSFSheet templateSheet,Profiles profile,Boolean amortizable) {
        try {
            final int CONSEPTO_ROW = 1;
            final int CONSEPTO_COL = 1;
            final int TABLE_ROW = 9;
            final int TABLE_COL = 0;


            // HEADER INFO
            XSSFRow conseptoRow = templateSheet.getRow(CONSEPTO_ROW);
            conseptoRow.createCell(CONSEPTO_COL).setCellValue(header.getConcept());
            conseptoRow = templateSheet.getRow(CONSEPTO_ROW + 1);
            conseptoRow.createCell(CONSEPTO_COL).setCellValue(header.getConceptObject());
            conseptoRow = templateSheet.getRow(CONSEPTO_ROW + 2);
            conseptoRow.createCell(CONSEPTO_COL).setCellValue(header.getProgram());
            if(amortizable){
                conseptoRow = templateSheet.getRow(CONSEPTO_ROW + 3);
                conseptoRow.createCell(CONSEPTO_COL).setCellValue(header.getEtapa());
            }
            conseptoRow = templateSheet.getRow(CONSEPTO_ROW + 4);
            conseptoRow.createCell(CONSEPTO_COL).setCellValue(header.getDate());

            int tableRowIndex = TABLE_ROW;
            for (DatosPlantilla row : dataPlantilla) {
                XSSFRow newRow = templateSheet.createRow(tableRowIndex);
                this.addInfoPlanillaRow(row, newRow,profile,amortizable);
                tableRowIndex++;
            }
            XSSFRow newRow = templateSheet.createRow(tableRowIndex);
            this.addInfoPlanillaTotalRow(newRow, dataPlantilla.stream().mapToDouble(DatosPlantilla::getSum).sum(),  dataPlantilla.stream().mapToInt(k-> k.getCant().intValue() ).sum(),profile);

        } catch (Exception e) {

            e.printStackTrace();
            ExcelManager.log.error("Error on generate datos planillaaaa: " + e.getMessage());
        }
        return templateSheet;
    }

    private XSSFRow addInfoPlanillaContableRow(InfoPlantillaContableRow dataRow, XSSFRow newRow) {

        newRow.createCell(0).setCellValue(dataRow.getConsecutivoDelDocumento());
        newRow.createCell(1).setCellValue(dataRow.getFechaDocumento());
        newRow.createCell(2).setCellValue(dataRow.getClaseDocumento());
        newRow.createCell(3).setCellValue(dataRow.getSociedad());
        newRow.createCell(4).setCellValue(dataRow.getFechaContabilidad());
        newRow.createCell(5).setCellValue(dataRow.getPeriodo());
        newRow.createCell(6).setCellValue(dataRow.getMoneda());
        newRow.createCell(7).setCellValue(dataRow.getReferencia());
        newRow.createCell(8).setCellValue(dataRow.getTextoCabeceraDoc());
        newRow.createCell(9).setCellValue(dataRow.getNumeroDeApunteContableDentroDelDocumento());
        newRow.createCell(10).setCellValue(dataRow.getClaveDeContabilidad());
        newRow.createCell(11).setCellValue(dataRow.getIndicadorCme());
        newRow.createCell(12).setCellValue(dataRow.getNumeroDeDeudorCliente());
        newRow.createCell(13).setCellValue(dataRow.getNumeroDeAcreedorProveedor());
        newRow.createCell(14).setCellValue(dataRow.getCuentaDeMayor());
        newRow.createCell(15).setCellValue(dataRow.getValorDeImporte());
        newRow.createCell(16).setCellValue(dataRow.getIndicadorDeImpuestosIva());
        newRow.createCell(17).setCellValue(dataRow.getFechaValor());
        newRow.createCell(18).setCellValue(dataRow.getFechaBaseParaVencimiento());
        newRow.createCell(19).setCellValue(dataRow.getClaveDeCondicionDePago());
        newRow.createCell(20).setCellValue(dataRow.getNumeroDeAsignacion());
        newRow.createCell(21).setCellValue(dataRow.getTexto());
        newRow.createCell(22).setCellValue(dataRow.getTextoNota());
        newRow.createCell(23).setCellValue(dataRow.getCentroDeBeneficio());
        newRow.createCell(24).setCellValue(dataRow.getCentroDeCosto());
        newRow.createCell(25).setCellValue(dataRow.getOrdenCo());
        newRow.createCell(26).setCellValue(dataRow.getClaveDeReferencia1());
        newRow.createCell(27).setCellValue(dataRow.getClaveDeReferencia2());
        newRow.createCell(28).setCellValue(dataRow.getClaveDeReferencia3());
        newRow.createCell(29).setCellValue(dataRow.getPosicionPresupuestaria());
        newRow.createCell(30).setCellValue(dataRow.getCentroGestor());
        newRow.createCell(31).setCellValue(dataRow.getArea());
        newRow.createCell(32).setCellValue(dataRow.getFondo());
        newRow.createCell(33).setCellValue(dataRow.getPrograma());
        newRow.createCell(34).setCellValue(dataRow.getDocumentoPresupuestal());
        newRow.createCell(35).setCellValue(dataRow.getIndicadorDeRetencionDeImpuesto());

        return newRow;
    }

    public Boolean sendUnavailableUsersNotification(File attachment, String email, String filename, String subject, String body) throws Exception{
        awsSESmanager.sendEmailWithAttachment(
                "no-reply@comfandi.com.co",
                email,
                subject,
                body,
                attachment,
                filename
        );
        return true;
    }

    public Boolean sendExcelFileByEmail(String url, String fileName, String email) throws Exception {
        File tempFile = File.createTempFile(fileName, ".xlsx");
        tempFile.deleteOnExit();
        ResponseInputStream<GetObjectResponse> s3InputStream = s3UploaderService.downloadFileAsStream(url);
        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = s3InputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return sendExcelEmail(tempFile, email,fileName.concat(".xlsx"));
    }

    private boolean sendExcelEmail(File attachment, String email,String fileName) throws Exception {

        awsSESmanager.sendEmailWithAttachment(
                "no-reply@comfandi.com.co",
                email,
                "Nueva cuenta generada",//todo:preguntar
                "Hola se ha generado una nueva cuenta para revision.", //todo:preguntar
                attachment,
                fileName
        );

        return true;
    }

    public String writeAndUpload(S3UploaderService s3UploaderService, File tempFile) throws IOException {
        String url = s3UploaderService.uploadPdfFile(tempFile, "korlon");
        ExcelManager.log.info("AWS S3 - Uploaded file URL: " + url);

        // Optionally delete the temp file
        //tempFile.delete();
        return url;
    }

    public void generateSheetUnavailableUsers(List<ApiInformationDatabase> rows,String email) throws Exception {
        ClassPathResource resource = new ClassPathResource(unavailablePath);
        try (InputStream fis = resource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastRowNum = 1;
            for (ApiInformationDatabase row : rows) {
                XSSFRow newRow = sheet.createRow(lastRowNum++);

                newRow.createCell(0).setCellValue(row.getCedula());
                newRow.createCell(1).setCellValue(row.getTipoDocumento());
                newRow.createCell(2).setCellValue(row.getNombreCompleto());
                newRow.createCell(3).setCellValue(row.getCorreoElectronico());
                newRow.createCell(4).setCellValue(row.getFechaRemision());
                newRow.createCell(5).setCellValue(row.getPorcentajeAvanceDelPrograma());
                newRow.createCell(6).setCellValue(row.getCharge());
                newRow.createCell(7).setCellValue(row.getObservations());
            }
            String isoSafeFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
            String fileName = "report_unavailable_users"+  isoSafeFilename; // + ".xlsx";

            File tempFile = new File(downloadPath + fileName + ".xlsx");

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
                ExcelManager.log.debug("Archivo Excel generado exitosamente.");
                sendUnavailableUsersNotification(tempFile,
                        email,
                        fileName+".xlsx",
                        "Usuarios Inhabilitados",
                        "Listado de usuarios inhabilitados");
            }finally {
                if(tempFile.exists()){
//                    boolean deleted=tempFile.delete();
//                    if(deleted) ExcelManager.log.info("arhivo de inhabilitados eliminado");
                }
            }
        } catch (Exception e) {
            ExcelManager.log.error("Error al procesar el archivo Excel:");
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public void generateSheetRevokedUsers(List<ApiInformationDatabase> rows,String email) throws Exception{
        ClassPathResource resource = new ClassPathResource(templatePath+"template_revoked.xlsx");
        try (InputStream fis = resource.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastRowNum = 1;
            for (ApiInformationDatabase row : rows) {
                XSSFRow newRow = sheet.createRow(lastRowNum++);
                newRow.createCell(0).setCellValue(row.getCedula());
                newRow.createCell(1).setCellValue(row.getNombreCompleto());
                newRow.createCell(2).setCellValue(row.getCorreoElectronico());
                newRow.createCell(3).setCellValue(row.getCharge());
                newRow.createCell(4).setCellValue(row.getObservations());

            }
            String isoSafeFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
            String fileName = "report_revoked_users"+  isoSafeFilename;

            File tempFile = new File(downloadPath + fileName + ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
                ExcelManager.log.debug("Archivo Excel generado exitosamente.");
                sendUnavailableUsersNotification(tempFile,
                        email,
                        fileName+".xlsx",
                        "Usuarios Rechazados",
                        "Listado de usuarios rechazados ");
            }finally {
                if(tempFile.exists()){
//                    boolean deleted=tempFile.delete();
//                    if(deleted) ExcelManager.log.info("arhivo de inhabilitados eliminado");
                }
            }
        } catch (Exception e) {
            ExcelManager.log.error("Error al procesar el archivo Excel:");
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public List<UserRevoked> validateRevokedUsersByFile(MultipartFile file,Long accountId){
        List<UserRevoked> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linea;
            int num=0;
            while ((linea = reader.readLine()) != null) {
                String[] columnas = linea.split(",");
                if(columnas.length<2) columnas = linea.split(";");
                if(num!=0){
                    users.add(UserRevoked.builder()
                            .accountId(accountId)
                            .document(columnas[1])
                            .documentType(columnas[2])
                            .status(columnas[3])
                            .reason(columnas[4]).build());
                }
                num++;
            }
        }catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
        return users;
    }


}
