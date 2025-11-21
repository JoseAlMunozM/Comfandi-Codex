package com.comfandi.korlon.manager.pdf;

import java.awt.Color;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.*;
import com.comfandi.korlon.enums.Profiles;
import com.comfandi.korlon.enums.Providers;
import com.comfandi.korlon.manager.data.DatosPlantilla;
import com.comfandi.korlon.mapper.DataSetMapper;
import com.comfandi.korlon.services.CourseService;
import com.comfandi.korlon.services.InfoPlantillaContableService;
import com.comfandi.korlon.services.TypificationService;
import com.comfandi.korlon.utils.Numbers;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;

import com.moebiusgames.pdfbox.table.PDFLabel;
import com.moebiusgames.pdfbox.table.PDFRenderContext;
import com.moebiusgames.pdfbox.table.PDFTable;
import com.moebiusgames.pdfbox.table.PDFTableRow;
import com.moebiusgames.pdfbox.table.PDFUtils;
import com.moebiusgames.pdfbox.table.TextType;

import com.comfandi.korlon.services.InfoDatosPlantillaService;
import com.comfandi.korlon.utils.LoggerKorlon;
import com.comfandi.korlon.utils.aws.AwsSESmanager;
import com.comfandi.korlon.utils.aws.S3UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Component
public class PDFManager {

	@Autowired
	private S3UploaderService s3UploaderService;

	Logger Log = LoggerKorlon.logger;
    @Autowired
    private AwsSESmanager awsSESmanager;
	@Value("${app.file-path-download}")
	private String downloadPath;

	@Autowired
	private TypificationService typificationService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private InfoDatosPlantillaService infoDatosPlanilla;

	@Autowired
	private DataSetMapper dataSetMapper;

	// Utility: Wrap text into lines that fit within specified width
	private static List<String> wrapText(String text, PDType1Font font, int fontSize, float width) throws IOException {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();

		for (String word : words) {
			String testLine = line + word + " ";
			float size = font.getStringWidth(testLine) / 1000 * fontSize;
			if (size > width) {
				lines.add(line.toString());
				line = new StringBuilder(word).append(" ");
			} else {
				line.append(word).append(" ");
			}
		}

		if (!line.isEmpty()) {
			lines.add(line.toString());
		}

		return lines;
	}
	
	private String generateFileName(String profile) {

		String isoSafeFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
		String filename = "documento_"+profile+"_" + isoSafeFilename;
		return filename;

	}
	
	public String writeAndUpload(S3UploaderService s3UploaderService, File tempFile) throws IOException {       
        String url = s3UploaderService.uploadFileFromDisk(tempFile, "korlon");
        Log.info("AWS S3 - Uploaded file URL: " + url);

        // Optionally delete the temp file
        //tempFile.delete();
        return url;
    }
	
	public static <T> T readFromDisk(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            @SuppressWarnings("unchecked")
            T obj = (T) ois.readObject();
            return obj;
        }
    }

	public Boolean sendPdfFileByEmail(String url, String fileName, String email) throws Exception {
		File tempFile = File.createTempFile(fileName, ".pdf");
		tempFile.deleteOnExit();
		ResponseInputStream<GetObjectResponse> s3InputStream = s3UploaderService.downloadFileAsStream(url);
		try (OutputStream out = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = s3InputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		return sendPdfEmail(tempFile, email,fileName.concat(".pdf"));
	}
	
	private boolean sendPdfEmail(File attachment,String email,String fileName) throws Exception {


        awsSESmanager.sendEmailWithAttachment(
                "no-reply@comfandi.com.co",
				email,
                "Nueva cuenta generada",
                "Hola se ha generado una nueva cuenta para revision.",
                attachment,
				fileName
        );
        
        return true;
	}
	public String generatePDFcomfandiJasper(ArrayList<ApiInformationDatabase> datosPlanilla, Profiles profiles, BillingAccountEntity ba, List<PortfolioEntity> portfolioList) throws Exception{
		InputStream reportStream = getClass().getResourceAsStream("/jasper/comfandi.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy", Locale.of("es", "CO"));
		String date = LocalDate.now().format(formatter);
		String logoPath = getClass().getResource("/jasper/comfandiLogo.png").toString();
		//List<TypificationEntity> typificationEntityList = typificationService.getTypificationByType(profiles.getValue());
		List<TypificationEntity> typificationEntityList = typificationService.getTypificationByType("activos-empresarial");
		List<DatosPlantilla> datosPlantillaList=profiles.getValue()
				.equals(Profiles.TH_FOSFEC.getValue())?infoDatosPlanilla.generateDatosPLantillaTH(datosPlanilla,typificationEntityList,null,false,portfolioList):infoDatosPlanilla.generateDatosPlanilla(datosPlanilla,typificationEntityList,null);
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataSetMapper.datosPlantillaListToDataSetList(datosPlantillaList));
		Double total = (Double) datosPlantillaList.stream().mapToDouble(DatosPlantilla::getSum).sum();
		String debeA="";
		if(profiles.getValue().equals(Profiles.TH_FOSFEC.getValue())){
			debeA="EL DPTO. FOMENTO EMPRESARIAL	COMPONENTE MAS TALENTO HUMANO";
		}else {
			Optional<CourseEntity> course = courseService.findCourse(ba.getBillingAccountId().longValue());
			if (course.isPresent()) {
				String provider = course.get().getProvider().getName();
				if (provider.equals(Providers.EMPRESARIAL.getValue())) {
					debeA = "UES FOMENTO EMPRESARIAL DESARROLLO EMPRESARIAL";
				} else if (provider.equals(Providers.EDUCACION.getValue())) {
					debeA = "UES EDUCACIÓN - UNIDAD DE OPORTUNIDADES EDUCATIVAS";
				}
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("cuentaCobro", ba.getBillingAccountId()+"-2");
		params.put("fecha", "Santiago de cali, "+date);
		TypificationEntity valuePDF=typificationEntityList.stream()
				.filter(x->x.getLocation().equals("CALI"))
				.filter(x-> x.getType().equals(profiles.getValue()))
				.findFirst().orElse(null);
		params.put("cebe","CEBE "+valuePDF.getCebe());
		params.put("cuentaContable", valuePDF.getAccount());
		params.put("debeA",debeA);
		params.put("totalEnLetras", Numbers.convertir(total));
		params.put("concepto","Prueba concepto");
		params.put("cuentaContable2","2705950132");
		params.put("nombreFirma","Lina María Martínez García");
		params.put("cargoFirma","Jefe de Desarrollo Empresarial");
		params.put("imageLogo",logoPath);
		params.put("nombreRevisor","Erika Gómez.");
		params.put("nombreAprobacion","Ingrid Tello");
		params.put("total",total);
		params.put("datosPlantilla",dataSource);

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

		String fileName = downloadPath + this.generateFileName(profiles.name()) + ".pdf";
		JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
		File tempFile = new File(fileName);
		String s3Url = this.writeAndUpload(s3UploaderService, tempFile );
		//this.sendPdfEmail(tempFile);
		return s3Url;
	}

}
