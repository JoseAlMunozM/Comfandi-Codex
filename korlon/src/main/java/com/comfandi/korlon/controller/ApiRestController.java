package com.comfandi.korlon.controller;

import com.comfandi.korlon.api.request.ApiDocumentEmailRequest;
import com.comfandi.korlon.api.request.ApiNotificationEmailRequest;
import com.comfandi.korlon.api.response.ApiBillingAccountResponse;
import com.comfandi.korlon.api.response.ApiDocumentNotificationResponse;
import com.comfandi.korlon.api.response.ApiDocumentsResponse;
import com.comfandi.korlon.api.response.ApiInformationDatabase;
import com.comfandi.korlon.entities.BillingAccountEntity;
import com.comfandi.korlon.entities.PortfolioEntity;
import com.comfandi.korlon.enums.Profiles;
import com.comfandi.korlon.enums.SourceType;
import com.comfandi.korlon.manager.data.PortfolioGroup;
import com.comfandi.korlon.services.BillingAccountService;
import com.comfandi.korlon.services.DocumentsService;
import com.comfandi.korlon.services.PortfolioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ApiRestController {

	@Autowired
	private DocumentsService documentsService;

	@Autowired
	private BillingAccountService billingAccountService;

	@Autowired
	private PortfolioService portfolioService;

	@PostMapping("/generate-documents")
	public ResponseEntity<?> generateDocuments(@RequestBody ArrayList<ApiInformationDatabase> data,
			@RequestParam String profile,
			@RequestParam String source) throws Exception {
		System.out.println("Generate documents");
		Optional<Profiles> profileData = Profiles.fromString(profile);
		Optional<SourceType> sourceType = SourceType.fromString(source);
		System.out.println("Profile: " + profile);
		System.out.println("Source: " + source);
		if (profileData.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid profile");
		}
		if (sourceType.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid source");
		}
		BillingAccountEntity ba = billingAccountService.createBillingAcount(data,
				Profiles.getLawByProfile(profileData.get()), profileData.get());
		List<PortfolioEntity> portfolio = Collections.emptyList();
		if (ba != null) {
			portfolio = portfolioService.find(data.stream()
					.filter(y -> y.getPortfolioId() != null)
					.map(x -> Long.parseLong(x.getPortfolioId()))
					.distinct()
					.map(id -> PortfolioGroup.builder().id(id).build())
					.toList());
		}
		System.out.println("<---> data: " + data + "<---> profileData.get: " + profileData.get() + "<---> sourceType.get: "
				+ sourceType.get() + "<---> ba: " + ba + "<---> portfolio: " + portfolio);
		String excelUrl = documentsService.generateExcelDocument(data, profileData.get(), sourceType.get(), ba,
				portfolio, false);
		String pdfUrl = documentsService.generatePDFDocument(data, profileData.get(), sourceType.get(), ba, portfolio);
		billingAccountService.updateBillingAccount(ba.getBillingAccountId(), excelUrl, pdfUrl);
		ApiDocumentsResponse response = new ApiDocumentsResponse("ok", excelUrl, pdfUrl);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/amortize-documents")
	public ResponseEntity<?> amortizeDocument(@RequestBody ArrayList<ApiInformationDatabase> data,
			@RequestParam String profile,
			@RequestParam String source, @RequestParam Long account) throws Exception {
		Optional<Profiles> profileData = Profiles.fromString(profile);
		Optional<SourceType> sourceType = SourceType.fromString(source);
		if (profileData.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid profile");
		}
		if (sourceType.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid source");
		}
		BillingAccountEntity ba = billingAccountService.getBillingAccount(account);
		List<PortfolioEntity> portfolio = Collections.emptyList();
		if (ba != null) {
			portfolio = portfolioService.find(data.stream()
					.filter(y -> y.getPortfolioId() != null)
					.map(x -> Long.parseLong(x.getPortfolioId()))
					.distinct()
					.map(id -> PortfolioGroup.builder().id(id).build())
					.toList());
		}
		String excelUrl = documentsService.generateExcelDocument(data, profileData.get(), sourceType.get(), ba,
				portfolio, true);
		ApiDocumentsResponse response = new ApiDocumentsResponse("ok", excelUrl, null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/regenerate-documents")
	public ResponseEntity<?> reGenerateDocuments(@RequestBody ArrayList<ApiInformationDatabase> data,
			@RequestParam String profile,
			@RequestParam String source, @RequestParam Long account, @RequestParam Boolean amortization)
			throws Exception {
		Optional<Profiles> profileData = Profiles.fromString(profile);
		Optional<SourceType> sourceType = SourceType.fromString(source);
		if (profileData.isEmpty()) {
			System.out.println("profile =" + profile);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid profile");
		}
		if (sourceType.isEmpty()) {
			System.out.println("source =" + source);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid source");
		}
		System.out.println("amortization " + amortization);
		BillingAccountEntity ba = billingAccountService.getBillingAccount(account);
		List<PortfolioEntity> portfolio = Collections.emptyList();
		if (ba != null) {
			portfolio = portfolioService.find(data.stream()
					.filter(y -> y.getPortfolioId() != null)
					.map(x -> Long.parseLong(x.getPortfolioId()))
					.distinct()
					.map(id -> PortfolioGroup.builder().id(id).build())
					.toList());
		}
		System.out.println("Portfolio list " + portfolio.size());
		String excelUrl = documentsService.generateExcelDocument(data, profileData.get(), sourceType.get(), ba,
				portfolio, amortization);
		String pdfUrl = documentsService.generatePDFDocument(data, profileData.get(), sourceType.get(), ba, portfolio);
		billingAccountService.updateBillingAccount(ba.getBillingAccountId(), excelUrl, pdfUrl);
		ApiDocumentsResponse response = new ApiDocumentsResponse("ok", excelUrl, pdfUrl);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/error")
	public String error() {
		return "Error handling";
	}

	@PostMapping("/send-documents")
	public ResponseEntity<ApiDocumentNotificationResponse> sendDocuments(@RequestBody ApiDocumentEmailRequest data)
			throws Exception {
		System.out.println("Send documents");
		return documentsService.sendDocumentByEmail(data);
	}

	@PostMapping("/unavailable-users/send-documents")
	public ResponseEntity<?> sendUnavailableDocuments(@RequestBody ApiNotificationEmailRequest notificationEmailRequest)
			throws Exception {
		System.out.println("Unavailable Send Documents");
		return documentsService.sendUnavailableUsersNotification(notificationEmailRequest.getData(),
				notificationEmailRequest.getEmail());
	}

	@PostMapping("/send-documents/revoked")
	public ResponseEntity<?> sendRevokedDocuments(@RequestBody ApiNotificationEmailRequest notificationEmailRequest)
			throws Exception {
		System.out.println("Revoked Send Documents");
		return documentsService.sendRevokedUsersNotification(notificationEmailRequest.getData(),
				notificationEmailRequest.getEmail());
	}

	@PostMapping("/send-documents/revoked-users-file")
	public ResponseEntity<?> sendRevokedDocuments(@RequestParam MultipartFile file, @RequestParam Long accountId)
			throws Exception {
		System.out.println("Revoked Users File");
		return documentsService.updateUsersByFile(file, accountId, true);
	}

	@PostMapping("/send-documents/update-users-file")
	public ResponseEntity<?> sendUsersDocuments(@RequestParam MultipartFile file, @RequestParam Long accountId)
			throws Exception {
		System.out.println("Update Users File");
		return documentsService.updateUsersByFile(file, accountId, false);
	}

	@GetMapping("/download-documents")
	public void awsDownload(@RequestParam String bucketName,
			@RequestParam String key,
			HttpServletResponse response) throws IOException {

		// Set response headers
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + key + "\"");

		// Stream the file content from S3 to response output stream
		try (InputStream inputStream = documentsService.downloadDocumentByKey(key);
				OutputStream out = response.getOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		}
	}

	@GetMapping("/download-documents/{id}")
	public ResponseEntity<InputStreamResource> generateAccountDocument(@PathVariable Long accountId) {
		return documentsService.getUsersDocument(accountId);
	}

}